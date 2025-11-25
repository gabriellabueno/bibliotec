package br.edu.fatecgru.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LibraryInterface extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("Bibliotec");

        stage.setScene(scene);

        stage.setMaximized(true);

        stage.setResizable(true);

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
