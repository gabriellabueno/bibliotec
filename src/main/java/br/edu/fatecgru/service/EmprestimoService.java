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


    // --- MÉTODOS AUXILIARES PARA NOVA REGRA ---

    /**
     * Define o prazo de empréstimo em dias com base no tipo de usuário.
     * @param usuario O usuário que está solicitando o empréstimo.
     * @return 14 dias para Docente, 7 dias para Aluno.
     */
    private int definirPrazoEmprestimo(Usuario usuario) {

        // Acesso direto ao estado do objeto, delegando a regra de negócio
        return usuario.isDocente() ? 14 : 7;
    }


    // Calcula a data prevista de devolução com base em um prazo em dias.
    private LocalDate calcularDataPrevistaDevolucao(LocalDate dataEmprestimo, int prazoDias) {
        return dataEmprestimo.plusDays(prazoDias);
    }

    /**
     * Registra um novo empréstimo no banco de dados (Lógica de Negócios Central)
     */
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