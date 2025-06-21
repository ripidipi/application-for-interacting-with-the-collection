package gui;

import io.Authentication;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import exceptions.EmptyLine;

public class LoginView {
    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Login");
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        root.getChildren().addAll(new Label("Username:"), userField, new Label("Password:"), passField, loginButton, registerButton);
        stage.setScene(new Scene(root, 300, 220));
        stage.show();

        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            try {
                if (username.isEmpty()) throw new EmptyLine("username");
                if (password.isEmpty()) throw new EmptyLine("password");
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                return;
            }
            Task<Boolean> loginTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return Authentication.login(username, password);
                }
            };
            loginTask.setOnSucceeded(ev -> {
                if (loginTask.getValue()) {
                    try {
                        new MainView(stage).show();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Authentication failed", ButtonType.OK).showAndWait();
                }
            });
            loginTask.setOnFailed(ev -> {
                String msg = loginTask.getException() != null && loginTask.getException().getMessage() != null
                        ? loginTask.getException().getMessage()
                        : "Unknown error";
                new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
            });
            new Thread(loginTask).start();
        });

        registerButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            try {
                if (username.isEmpty()) throw new EmptyLine("username");
                if (password.isEmpty()) throw new EmptyLine("password");
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                return;
            }
            Task<Boolean> regTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return Authentication.register(username, password);
                }
            };
            regTask.setOnSucceeded(ev -> {
                if (regTask.getValue()) {
                    try {
                        new MainView(stage).show();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Registration failed", ButtonType.OK).showAndWait();
                }
            });
            regTask.setOnFailed(ev -> {
                String msg = regTask.getException() != null && regTask.getException().getMessage() != null
                        ? regTask.getException().getMessage()
                        : "Unknown error";
                new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
            });
            new Thread(regTask).start();
        });
    }
}
