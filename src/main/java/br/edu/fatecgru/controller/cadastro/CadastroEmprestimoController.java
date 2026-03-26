package br.edu.fatecgru.controller.cadastro;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import br.edu.fatecgru.service.EmprestimoService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CadastroEmprestimoController implements Initializable {


    private final EmprestimoService emprestimoService = new EmprestimoService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @FXML private ToggleGroup materialTypeGroup;
    @FXML private RadioButton rbLivro;
    @FXML private RadioButton rbRevista;
    @FXML private RadioButton rbTG;
    @FXML private RadioButton rbEquipamento;


    @FXML private TextField usuario;
    @FXML private TextField material;
    @FXML private TextField dataEmprestimoField;



    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Preenche a data de empréstimo como a data atual

        dataEmprestimoField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataEmprestimoField.setDisable(true);
    }



    @FXML
    private void onCadastrarClick(ActionEvent event) {

        String usuarioIdStr = usuario.getText();
        String materialCodStr = material.getText();
        TipoMaterial tipoMaterial;

        RadioButton selectedRb = (RadioButton) materialTypeGroup.getSelectedToggle();

        switch (selectedRb.getId()) {
            case "rbLivro":
                tipoMaterial = TipoMaterial.LIVRO;
                break;
            case "rbRevista":
                tipoMaterial = TipoMaterial.REVISTA;
                break;
            case "rbTG":
                tipoMaterial = TipoMaterial.TG;
                break;
            case "rbEquipamento":
                tipoMaterial = TipoMaterial.EQUIPAMENTO;
                break;
            default:
                mostrarAlerta(AlertType.ERROR, "Erro de Configuração", "Tipo de material selecionado é inválido.");
                return;
        }

        if (usuarioIdStr == null || usuarioIdStr.trim().isEmpty() ||
                materialCodStr == null || materialCodStr.trim().isEmpty()) {

            mostrarAlerta(AlertType.ERROR, "Erro de Validação", "Os IDs de Usuário e Material são obrigatórios.");
            return;
        }

        try {

            Emprestimo emprestimoSalvo = emprestimoService.registrarEmprestimo(usuarioIdStr.trim(), materialCodStr, tipoMaterial);

            if (emprestimoSalvo != null) {
                mostrarPopUpSucesso(emprestimoSalvo);

                usuario.clear();
                material.clear();

            } else {
                mostrarAlerta(AlertType.ERROR, "Falha", "Não foi possível registrar o empréstimo.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Os IDs devem ser válidos.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Erros de entidade não encontrada ou indisponibilidade
            mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());

        } catch (Exception e) {
            // Erros de persistência
            mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostrarAlerta(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void mostrarPopUpSucesso(Emprestimo emprestimo) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("✅ Empréstimo Registrado");
        alert.setHeaderText("Detalhes do Empréstimo");

        // Formata as datas
        String dataEmprestimo = emprestimo.getDataEmprestimo().format(formatter);
        String dataPrevista = emprestimo.getDataPrevistaDevolucao().format(formatter);

        // Obtém o nome/título do material
        String nomeMaterial = getNomeOuTitulo(emprestimo.getMaterial());

        // Constrói o conteúdo detalhado
        String content = String.format(
                "----------------------------------------------\n" +
                        "USUÁRIO:\n" +
                        "  ID: %s\n" +
                        "  Nome: %s\n" +
                        "----------------------------------------------\n" +
                        "MATERIAL EMPRESTADO:\n" +
                        "  Tipo: %s\n" +
                        "  Código: %s\n" +
                        "  Título/Nome: %s\n" +
                        "----------------------------------------------\n" +
                        "DATAS:\n" +
                        "  Data do Empréstimo: %s\n" +
                        "  Previsão de Devolução: %s\n" +
                        "----------------------------------------------\n" +
                        "STATUS INICIAL: ATIVO",
                emprestimo.getUsuario().getIdUsuario(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getMaterial().getTipoMaterial(),
                emprestimo.getMaterial().getIdMaterial(),
                nomeMaterial,
                dataEmprestimo,
                dataPrevista
        );

        alert.setContentText(content);
        alert.setResizable(true); // Permite redimensionar se o conteúdo for longo
        alert.showAndWait();
    }

    // Auxiliar para extrair o nome ou título específico da subclasse do Material.

    private String getNomeOuTitulo(Material material) {
        if (material == null) return "Material Não Encontrado";

        if (material instanceof Livro livro) {
            return livro.getTitulo();
        } else if (material instanceof Revista revista) {
            return revista.getTitulo();
        } else if (material instanceof TG tg) {
            return tg.getTitulo();
        } else if (material instanceof Equipamento equipamento) {
            return equipamento.getNome();
        }
        return "Material Genérico/Desconhecido";
    }
}