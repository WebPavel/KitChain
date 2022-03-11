package com.kangsting.kitchain;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KitChainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KitChainApplication.class.getResource("Kit.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        KitController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);
        stage.setTitle("常用工具箱!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}