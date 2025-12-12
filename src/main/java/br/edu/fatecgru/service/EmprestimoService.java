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

    // --- MÉTODOS AUXILIARES PARA NOVA REGRA ---

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

    /**
     * Aplica penalidade ao usuário se o empréstimo estiver atrasado.
     */
    public void verificarEaplicarAtraso(Emprestimo emprestimo) throws Exception {
        if (emprestimo.getStatusEmprestimo() != StatusEmprestimo.ATIVO) return;

        LocalDate hoje = LocalDate.now();
        if (emprestimo.getDataPrevistaDevolucao().isBefore(hoje)) {

            // 1. Atualiza Status do Empréstimo
            emprestimo.setStatusEmprestimo(StatusEmprestimo.ATRASADO);
            emprestimoRepository.cadastrarEmprestimo(emprestimo);

            // 2. Aplica Penalidade ao Usuário
            Usuario usuario = emprestimo.getUsuario();
            usuario.setPenalidade(true);
            usuario.setDataFimPenalidade(hoje.plusDays(DIAS_PENALIDADE)); // Nova regra assumida
            usuarioRepository.cadastrarUsuario(usuario); // Reutilizando para update
        }
    }

    /**
     * Registra a devolução do material, atualizando status do empréstimo e material.
     * @param emprestimo O empréstimo a ser finalizado.
     * @param dataDevolucao A data real de devolução.
     */
    public void registrarDevolucao(Emprestimo emprestimo, LocalDate dataDevolucao) throws IllegalStateException, Exception {
        if (emprestimo.getDataDevolucao() != null) {
            throw new IllegalStateException("O empréstimo já foi devolvido.");
        }

        // 1. Atualiza Empréstimo
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatusEmprestimo(StatusEmprestimo.DEVOLVIDO);
        emprestimoRepository.cadastrarEmprestimo(emprestimo);

        // 2. Atualiza Material
        Material material = emprestimo.getMaterial();
        material.setStatusMaterial(StatusMaterial.DISPONIVEL);
        materialRepository.cadastrarMaterial(material); // Reutilizando para update
    }

    /**
     * Renova o empréstimo, estendendo a data prevista de devolução.
     */
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

        // 1. Determina o novo prazo (usa a regra do usuário novamente)
        Usuario usuario = emprestimo.getUsuario();
        int prazoDias = definirPrazoEmprestimo(usuario);
        LocalDate novaDataPrevista = emprestimo.getDataPrevistaDevolucao().plusDays(prazoDias);

        // 2. Atualiza Empréstimo
        emprestimo.setDataPrevistaDevolucao(novaDataPrevista);
        emprestimo.setRenovado(true); // Assume que este setter existe
        emprestimoRepository.cadastrarEmprestimo(emprestimo);
    }

    /**
     * Exclui o registro de empréstimo e reverte o status do material.
     */
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

        // --- VALIDAÇÃO DE OBRIGATÓRIOS (IDs de Entrada) ---
        if (idUsuario == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: O ID do Usuário é obrigatório.");
        }
        if (codMaterial == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: O Código do Material é obrigatório.");
        }
        if (tipoMaterial == null) {
            throw new IllegalArgumentException("EMPRÉSTIMO: É necessário selecionar o Tipo do Material.");
        }

        // 1. Buscar entidades e validar existência
        Long idMaterial = materialRepository.buscarIdPorCodigoETipo(codMaterial, tipoMaterial);

        Usuario usuario = usuarioRepository.buscarUsuarioPorId(idUsuario);
        Material material = materialRepository.buscarMaterialPorId(idMaterial);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material não encontrado.");
        }

        // 2. Validação de Disponibilidade

        // Verificar Penalidade
        if (usuario.isPenalidade()) { // Assumindo o método isPenalidade()
            throw new IllegalStateException("Usuário está penalizado e não pode realizar novos empréstimos.");
        }

        // 2. Verificar Limite de Empréstimos (Máximo 3)
        Long qtdEmprestimosAtivos = emprestimoRepository.contarEmprestimosAtivosPorUsuario(idUsuario);

        final int limiteMaximo = usuario.isDocente() ? 3 : 2;

        if (qtdEmprestimosAtivos >= limiteMaximo) {
            throw new IllegalStateException("Limite de empréstimos atingido. O usuário possui " + qtdEmprestimosAtivos + "/" + limiteMaximo + " empréstimos ativos.");
        }

        if (material.getStatusMaterial() != StatusMaterial.DISPONIVEL) {
            throw new IllegalStateException("Material indisponível. Status: " + material.getStatusMaterial());
        }

        // 3. Preparar Empréstimo e Atualizar Status (O material é atualizado antes de ser enviado ao Repository)
        material.setStatusMaterial(StatusMaterial.EMPRESTADO);


        // Determina o prazo com base no usuário
        LocalDate hoje = LocalDate.now();
        int prazoDias = definirPrazoEmprestimo(usuario);
        LocalDate dataPrevistaDevolucao = calcularDataPrevistaDevolucao(hoje, prazoDias);

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setMaterial(material);
        novoEmprestimo.setDataEmprestimo(hoje);
        novoEmprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);

        // 4. Persistir Empréstimo (Delegado ao Repository)


//        // 5. Atualizar Status do Material (Delegado ao Repository)
//        material.setStatusMaterial(StatusMaterial.EMPRESTADO);
//        materialRepository.cadastrarMaterial(material); // Reutiliza o método de salvar do MaterialRepository para o merge

        return emprestimoRepository.cadastrarEmprestimo(novoEmprestimo);
    }
}