package br.edu.fatecgru.service;

import br.edu.fatecgru.model.Entity.Emprestimo;
import br.edu.fatecgru.model.Entity.Material;
import br.edu.fatecgru.model.Entity.Usuario;
import br.edu.fatecgru.model.Enum.StatusEmprestimo;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.repository.EmprestimoRepository;
import br.edu.fatecgru.repository.MaterialRepository;
import br.edu.fatecgru.repository.UsuarioRepository;
import br.edu.fatecgru.util.JPAUtil;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;

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
    public Emprestimo registrarEmprestimo(Long idUsuario, Long idMaterial) throws IllegalArgumentException, IllegalStateException, Exception {

        // 1. Buscar entidades e validar existência (CHAMA REPOSITORY)
        Usuario usuario = usuarioRepository.buscarUsuarioPorId(idUsuario);
        Material material = materialRepository.buscarMaterialPorId(idMaterial);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material não encontrado.");
        }

        // 2. Validação de Disponibilidade
        if (material.getStatusMaterial() != StatusMaterial.DISPONIVEL) {
            throw new IllegalStateException("Material indisponível. Status: " + material.getStatusMaterial());
        }

        // 3. Preparar Empréstimo (LÓGICA DE NEGÓCIOS)


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
        Emprestimo emprestimoPersistido = emprestimoRepository.cadastrarEmprestimo(novoEmprestimo);

        // 5. Atualizar Status do Material (Delegado ao Repository)
        material.setStatusMaterial(StatusMaterial.EMPRESTADO);
        materialRepository.cadastrarMaterial(material); // Reutiliza o método de salvar do MaterialRepository para o merge

        return emprestimoPersistido;
    }
}