package uet.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uet.client.ClientMain;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class RegisterTypeController {

    @FXML
    private HBox btnCaNhan;

    @FXML
    private HBox btnToChuc;

    @FXML
    void handleRegisterCaNhan(MouseEvent event) {
        ClientMain.switchTo("LoginView.fxml",600,500);
    }

    @FXML
    void handleRegisterToChuc(MouseEvent event) {
        ClientMain.switchTo("LoginView.fxml",600,500);
    }

    private void switchScene(MouseEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRegisterToChuc(javafx.scene.input.MouseEvent mouseEvent) {
    }

    public void handleRegisterCaNhan(javafx.scene.input.MouseEvent mouseEvent) {
    }
}