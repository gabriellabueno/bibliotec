package br.edu.fatecgru.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import lombok.NoArgsConstructor;

import java.util.function.UnaryOperator;

@NoArgsConstructor
public final class InterfaceUtil {

    public static void aplicarRestricaoNumerica(TextField textField) {
        // Cria um UnaryOperator que aceita apenas dígitos (0-9)
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(integerFilter));
    }


    public static void aplicarMascaraTamanhoFixo(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    public static void aplicarMascaraISBN(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals(oldValue)) {
                return;
            }

            // Remove tudo que não for dígito
            String digits = newValue.replaceAll("\\D", "");


            if (digits.length() > 13) {
                digits = digits.substring(0, 13);
            }

            // Reconstrói a String aplicando a formatação
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                char c = digits.charAt(i);
                formatted.append(c);

                // Adiciona os traços nas posições corretas
                if (i == 2 || i == 4 || i == 9 || i == 11) {
                    if (i < digits.length() - 1) {
                        formatted.append("-");
                    }
                }
            }

            if (!formatted.toString().equals(newValue)) {
                textField.setText(formatted.toString());
                textField.positionCaret(formatted.length());
            }
        });
    }

    public static void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
