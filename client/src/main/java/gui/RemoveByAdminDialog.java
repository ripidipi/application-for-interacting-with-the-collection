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
import service.Localization;
import storage.Request;
import storage.Logging;
import io.Server;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class RemoveByAdminDialog {
    private final MainView parent;
    private final ResourceBundle bundle = Localization.bundle();

    public RemoveByAdminDialog(MainView parent) {
        this.parent = parent;
    }

    public void show() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(bundle.getString("removeadmin.title"));
        dialog.setHeaderText(bundle.getString("removeadmin.header"));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        DatePicker birthdayPicker = new DatePicker();
        TextField heightField = new TextField();
        TextField passportField = new TextField();

        grid.add(new Label(bundle.getString("removeadmin.label.name")), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label(bundle.getString("removeadmin.label.birthday")), 0, 1);
        grid.add(birthdayPicker, 1, 1);
        grid.add(new Label(bundle.getString("removeadmin.label.height")), 0, 2);
        grid.add(heightField, 1, 2);
        grid.add(new Label(bundle.getString("removeadmin.label.passport")), 0, 3);
        grid.add(passportField, 1, 3);

        ButtonType removeBtn = new ButtonType(bundle.getString("removeadmin.button.remove"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, removeBtn);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == removeBtn) {
                try {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) throw new EmptyLine(bundle.getString("removeadmin.key.name"));
                    LocalDate date = birthdayPicker.getValue();
                    if (date == null) throw new EmptyLine(bundle.getString("removeadmin.key.birthday"));
                    LocalDateTime birthday = date.atStartOfDay();
                    if (birthday.isAfter(LocalDateTime.now())) throw new DataInTheFuture(bundle.getString("removeadmin.key.birthday"));

                    String heightTxt = heightField.getText().trim();
                    Double height = heightTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                            bundle.getString("removeadmin.key.height"), Double.class,
                            false, true, false, heightTxt,
                            false, null, false
                    );

                    String passport = passportField.getText().trim();
                    Person admin = new Person(name, birthday, height, passport);

                    String response = ClientService.removeByAdminDialog(admin);
                    new Alert(Alert.AlertType.INFORMATION,
                            DistributionOfTheOutputStream.printFromServer(response), ButtonType.OK
                    ).showAndWait();
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
