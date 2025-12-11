package br.edu.fatecgru.controller;

import br.edu.fatecgru.controller.cadastro.CadastroNotaFiscalController;
import br.edu.fatecgru.controller.gerenciamento.GerenciamentoMaterialController;
import br.edu.fatecgru.controller.pesquisa.PesquisaMaterialController;
import br.edu.fatecgru.controller.pesquisa.PesquisaUsuarioController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    // PARA A TELA DE CADASTRO DE NOTA FISCAL NÃO FECHAR APÓS CADASTRAR
    public void loadNotaFiscalScreen(String fxmlPath) {
        loadScreenWithCallback( fxmlPath,
                (CadastroNotaFiscalController controller) -> {
                    controller.setIsModal(false);
                }
        );
    }

    public void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newScreen = loader.load();

            Object controller = loader.getController();

            // 1. Injeção para PesquisaMaterialController (Já existente)
            if (controller instanceof PesquisaMaterialController pesquisaMaterialController) {
                pesquisaMaterialController.setMainController(this);
            }

            // 🎯 2. CORREÇÃO: ADICIONAR INJEÇÃO PARA PesquisaUsuarioController
            if (controller instanceof PesquisaUsuarioController pesquisaUsuarioController) {
                pesquisaUsuarioController.setMainController(this);
            }
            // -----------------------------------------------------------

            mainBorderPane.setCenter(newScreen);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + fxmlFile);
        }
    }

    public <T extends Initializable> void loadScreenWithCallback(String fxmlPath, Consumer<T> callback) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            System.out.println("Tentando carregar: " + fxmlPath);
            System.out.println("URL encontrada: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERRO: Arquivo FXML não encontrado: " + fxmlPath);
                System.err.println("Verifique se o arquivo está em src/main/resources" + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            Pane newScreen = loader.load();

            T controller = loader.getController();

            if (controller != null && callback != null) {
                callback.accept(controller);
            }

            if (mainBorderPane != null) {
                mainBorderPane.setCenter(newScreen);
            } else {
                System.err.println("ERRO: mainBorderPane não foi injetado (null). A tela não será exibida.");
            }

        } catch (Exception e) {
            // 4. TRATAMENTO DE ERROS VISÍVEL
            System.err.println("Erro ao carregar tela com callback: " + fxmlPath);
            e.printStackTrace(); // Crucial para ver NullPointerExceptions ou IOExceptions.
        }
    }

    @FXML
    public void initialize() {
        loadScreen("/ui/screens/home.fxml");
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/ui/side-bar.fxml"));
            Pane sidebar = sidebarLoader.load();

            SideBarController sidebarController = sidebarLoader.getController(); // Pega o controller
            sidebarController.setMainController(this); // Injeta referência

            mainBorderPane.setLeft(sidebar); // Coloca a barra lateral na tela
        } catch (IOException e) {
            System.err.println("Erro ao carregar a barra lateral.");
            e.printStackTrace();
        }
    }
}
