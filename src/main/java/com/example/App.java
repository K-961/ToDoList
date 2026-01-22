package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/example/main.fxml")
        );

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                App.class.getResource("/com/example/style.css").toExternalForm()
        );

        stage.setTitle("MyApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
