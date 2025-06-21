package gui;

import commands.ExecuteScript;
import exceptions.ServerDisconnect;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import storage.Logging;
import java.util.ResourceBundle;

import static service.Localization.bundle;

public class ExecuteScriptDialog {
    private final MainView parent;
    private final ResourceBundle msg = bundle();

    public ExecuteScriptDialog(MainView parent) {
        this.parent = parent;
    }

    public void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(msg.getString("execscript.title"));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label pathLabel = new Label(msg.getString("execscript.label.path"));
        TextField pathField = new TextField();
        Button runBtn = new Button(msg.getString("execscript.button.run"));

        grid.add(pathLabel, 0, 0);
        grid.add(pathField, 1, 0);
        grid.add(runBtn, 1, 1);

        runBtn.setOnAction(e -> {
            String filePath = pathField.getText().trim();
            if (filePath.isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                        msg.getString("execscript.error.emptyPath"),
                        ButtonType.OK
                ).showAndWait();
                return;
            }
            try {
                ExecuteScript.executeScript(filePath);
            } catch (ServerDisconnect ex) {
                new Alert(Alert.AlertType.ERROR,
                        msg.getString("execscript.error.serverDisconnect") + ": " + ex.getMessage(),
                        ButtonType.OK
                ).showAndWait();
            } catch (Exception ex) {
                Logging.log(Logging.makeMessage(ex.getMessage(), ex.getStackTrace()));
            }
            new Alert(Alert.AlertType.INFORMATION,
                    msg.getString("execscript.info.finished"),
                    ButtonType.OK
            ).showAndWait();
            dialog.close();
            parent.handleRefresh();
        });

        Scene scene = new Scene(grid, 400, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
