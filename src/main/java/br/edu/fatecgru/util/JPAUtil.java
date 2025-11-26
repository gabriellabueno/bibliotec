package br.edu.fatecgru.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Nome da unidade de persistência (deve bater com o persistence.xml)
    private static final String PERSISTENCE_UNIT_NAME = "FatecPU";

    // Objeto principal, criado apenas uma vez
    private static EntityManagerFactory factory;

    // Bloco estático: Inicializado quando a classe é carregada pela JVM
    static {
        try {
            // Cria o factory, lendo o persistence.xml
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("JPA: Falha ao inicializar o EntityManagerFactory.");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e); // Erro fatal na inicialização
        }
    }

    /**
     * Retorna uma nova instância do EntityManager para cada transação.
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (factory == null) {
            throw new IllegalStateException("EntityManagerFactory não inicializado. Verifique o persistence.xml.");
        }
        return factory.createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory. Deve ser chamado ao encerrar a aplicação.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            System.out.println("JPA: EntityManagerFactory fechado.");
        }
    }
}