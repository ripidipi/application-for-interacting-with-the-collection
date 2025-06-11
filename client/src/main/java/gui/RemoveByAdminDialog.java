package gui;

import collection.Person;
import collection.fabrics.PersonFabric;
import commands.Commands;
import exceptions.*;
import io.DistributionOfTheOutputStream;
import io.PrimitiveDataTransform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ClientService;
import storage.Request;
import storage.Logging;
import io.Server;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RemoveByAdminDialog {
    private final MainView parent;

    public RemoveByAdminDialog(MainView parent) {
        this.parent = parent;
    }

    public void show() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Remove any by Admin");
        dialog.setHeaderText("Enter administrator details");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField adminNameField       = new TextField();
        DatePicker birthdayPicker = new DatePicker();
        TextField adminHeightField     = new TextField();
        TextField adminPassportField   = new TextField();

        grid.add(new Label("Admin Name:"), 0, 0);
        grid.add(adminNameField, 1, 0);
        grid.add(new Label("Birthday (dd/MM/yyyy):"), 0, 1);
        grid.add(birthdayPicker, 1, 1);
        grid.add(new Label("Height (optional):"), 0, 2);
        grid.add(adminHeightField, 1, 2);
        grid.add(new Label("PassportID:"), 0, 3);
        grid.add(adminPassportField, 1, 3);

        ButtonType removeBtn = new ButtonType("Remove");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, removeBtn);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == removeBtn) {
            try {
                String name = adminNameField.getText().trim();
                LocalDate date = birthdayPicker.getValue();
                String heightTxt = adminHeightField.getText().trim();
                String passport = adminPassportField.getText().trim();

                if (name.isEmpty()) throw new EmptyLine("admin name");
                if (date == null) throw new EmptyLine("birthday");

                LocalDateTime birthday = date.atStartOfDay();
                if (birthday.isAfter(LocalDateTime.now())) {
                    throw new DataInTheFuture("birthday");
                }

                Double height = heightTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                        "admin height", Double.class, false,
                        true, false, heightTxt, false, null, false
                );

                Person admin = new Person(name, birthday, height, passport);
                String response = ClientService.removeByAdminDialog(admin);
                new Alert(Alert.AlertType.INFORMATION, DistributionOfTheOutputStream.printFromServer(response), ButtonType.OK).showAndWait();
                parent.handleRefresh();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
            }
            return null;
        });

        dialog.showAndWait();
    }
}