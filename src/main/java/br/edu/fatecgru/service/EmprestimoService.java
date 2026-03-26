package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import br.edu.fatecgru.repository.EmprestimoRepository;
import br.edu.fatecgru.repository.MaterialRepository;
import br.edu.fatecgru.repository.UsuarioRepository;

import java.time.LocalDate;

public class EmprestimoService {


    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final MaterialRepository materialRepository = new MaterialRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

    private static final int DIAS_PENALIDADE = 7;



    private int definirPrazoEmprestimo(Usuario usuario) {
        return usuario.isDocente() ? 14 : 7;
    }

    private LocalDate calcularDataPrevistaDevolucao(LocalDate dataEmprestimo, int prazoDias) {
        return dataEmprestimo.plusDays(prazoDias);
    }


    public void atualizarStatus(Emprestimo emprestimo, StatusEmprestimo novoStatus) throws Exception {
        if (emprestimo == null || novoStatus == null) return;
        emprestimo.setStatusEmprestimo(novoStatus);
        emprestimoRepository.cadastrarEmprestimo(emprestimo); // Reutilizando para update
    }


    public void verificarEaplicarAtraso(Emprestimo emprestimo) throws Exception {
        if (emprestimo.getStatusEmprestimo() != StatusEmprestimo.ATIVO) return;

        LocalDate hoje = LocalDate.now();
        if (emprestimo.getDataPrevistaDevolucao().isBefore(hoje)) {


            emprestimo.setStatusEmprestimo(StatusEmprestimo.ATRASADO);
            emprestimoRepository.cadastrarEmprestimo(emprestimo);


            Usuario usuario = emprestimo.getUsuario();
            usuario.setPenalidade(true);
            usuario.setDataFimPenalidade(hoje.plusDays(DIAS_PENALIDADE)); // Nova regra assumida
            usuarioRepository.atualizarUsuario(usuario); // Reutilizando para update
        }
    }


    public void registrarDevolucao(Emprestimo emprestimo, LocalDate dataDevolucao) throws IllegalStateException, Exception {
        if (emprestimo.getDataDevolucao() != null) {
            throw new IllegalStateException("O empréstimo já foi devolvido.");
        }


        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatusEmprestimo(StatusEmprestimo.DEVOLVIDO);


        Material material = emprestimo.getMaterial();
        material.setStatusMaterial(StatusMaterial.DISPONIVEL);

        emprestimoRepository.cadastrarEmprestimo(emprestimo);
    }


    public void renovarEmprestimo(Emprestimo emprestimo) throws IllegalStateException, Exception {
        if (emprestimo.isRenovado()) { // Assumindo isRenovado existe no Emprestimo
            throw new IllegalStateException("O empréstimo só pode ser renovado uma vez.");
        }
        if (emprestimo.getDataDevolucao() != null) {
            throw new IllegalStateException("Não é possível renovar um empréstimo já devolvido.");
        }
        if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.ATRASADO) {
            throw new IllegalStateException("Não é possível renovar um empréstimo atrasado. É necessária a devolução.");
        }


        Usuario usuario = emprestimo.getUsuario();
        int prazoDias = definirPrazoEmprestimo(usuario);
        LocalDate novaDataPrevista = emprestimo.getDataPrevistaDevolucao().plusDays(prazoDias);


        emprestimo.setDataPrevistaDevolucao(novaDataPrevista);
        emprestimo.setRenovado(true); // Assume que este setter existe
        emprestimoRepository.cadastrarEmprestimo(emprestimo);
    }


    public void excluirEmprestimo(Emprestimo emprestimo) throws IllegalStateException, Exception {
        if (emprestimo.getDataDevolucao() == null) {
            // Se o empréstimo estava ativo, o material deve voltar a ser DISPONÍVEL
            Material material = emprestimo.getMaterial();
            material.setStatusMaterial(StatusMaterial.DISPONIVEL);
            materialRepository.cadastrarMaterial(material);
        }

        emprestimoRepository.excluirEmprestimo(emprestimo.getIdEmprestimo()); // Assume que existe o método no Repository
    }



    public Emprestimo registrarEmprestimo(String idUsuario, String codMaterial, TipoMaterial tipoMaterial) throws IllegalArgumentException, IllegalStateException, Exception {


        if (idUsuario == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: O ID do Usuário é obrigatório.");
        }
        if (codMaterial == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: O Código do Material é obrigatório.");
        }
        if (tipoMaterial == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: É necessário selecionar o Tipo do Material.");
        }


        Long idMaterial = materialRepository.buscarIdPorCodigoETipo(codMaterial, tipoMaterial);

        Usuario usuario = usuarioRepository.buscarUsuarioPorId(idUsuario);
        Material material = materialRepository.buscarMaterialPorId(idMaterial);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material não encontrado.");
        }




        if (usuario.isPenalidade()) {

            LocalDate hoje = LocalDate.now();
            LocalDate fim = usuario.getDataFimPenalidade();


            if (fim != null && fim.isBefore(hoje)) {
                usuario.setPenalidade(false);
                usuario.setDataFimPenalidade(null);
                usuarioRepository.atualizarUsuario(usuario);
            } else if (fim == null || fim.isAfter(hoje) || fim.isEqual(hoje)) {

                String fimStr = (fim != null) ? fim.toString() : "sem data registrada";
                throw new IllegalStateException(
                        "Usuário está sob penalidade até " + fimStr + " e não pode realizar novos empréstimos."
                );
            }
        }


        Long qtdEmprestimosAtivos = emprestimoRepository.contarEmprestimosAtivosPorUsuario(idUsuario);

        final int limiteMaximo = usuario.isDocente() ? 3 : 2;

        if (qtdEmprestimosAtivos >= limiteMaximo) {
            throw new IllegalStateException("Limite de empréstimos atingido. O usuário possui " + qtdEmprestimosAtivos + "/" + limiteMaximo + " empréstimos ativos.");
        }

        if (material.getStatusMaterial() != StatusMaterial.DISPONIVEL) {
            throw new IllegalStateException("Material indisponível. Status: " + material.getStatusMaterial());
        }


        material.setStatusMaterial(StatusMaterial.EMPRESTADO);



        LocalDate hoje = LocalDate.now();
        int prazoDias = definirPrazoEmprestimo(usuario);
        LocalDate dataPrevistaDevolucao = calcularDataPrevistaDevolucao(hoje, prazoDias);

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setMaterial(material);
        novoEmprestimo.setDataEmprestimo(hoje);
        novoEmprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);


        return emprestimoRepository.cadastrarEmprestimo(novoEmprestimo);
    }
}