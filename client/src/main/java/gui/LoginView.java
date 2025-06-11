package gui;

import io.Authentication;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import exceptions.EmptyLine;
import exceptions.IncorrectValue;
import exceptions.RemoveOfTheNextSymbol;
import exceptions.ZeroValue;

/**
 * Login and registration view using GUI, with validation via PrimitiveDataTransform.
 */
public class LoginView {
    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Login");
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        root.getChildren().addAll(userLabel, userField, passLabel, passField, loginButton, registerButton);

        stage.setScene(new Scene(root, 300, 220));
        stage.show();

        loginButton.setOnAction(e -> {
            try {
                String username = userField.getText().trim();
                String password = passField.getText().trim();
                if(username.isEmpty()) throw new EmptyLine("username");
                if(password.isEmpty()) throw new EmptyLine("password");

                boolean ok = Authentication.login(username, password);
                if (ok) {
                    new MainView(stage).show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Authentication failed", ButtonType.OK).showAndWait();
                }
            } catch (EmptyLine | IncorrectValue | RemoveOfTheNextSymbol | ZeroValue ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        registerButton.setOnAction(e -> {
            try {
                String username = userField.getText().trim();
                String password = passField.getText().trim();
                if(username.isEmpty()) throw new EmptyLine("username");
                if(password.isEmpty()) throw new EmptyLine("password");

                boolean success = Authentication.register(username, password);
                if (success) {
                    new MainView(stage).show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Registration failed", ButtonType.OK).showAndWait();
                }
            } catch (EmptyLine | IncorrectValue | RemoveOfTheNextSymbol | ZeroValue ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }
}