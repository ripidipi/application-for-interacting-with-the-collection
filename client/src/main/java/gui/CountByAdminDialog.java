package gui;

import io.DistributionOfTheOutputStream;
import service.ClientService;
import collection.Person;
import io.PrimitiveDataTransform;
import io.Authentication;
import exceptions.EmptyLine;
import exceptions.DataInTheFuture;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static service.Localization.bundle;

public class CountByAdminDialog {
    private final MainView parent;
    private final ResourceBundle msg = bundle();

    public CountByAdminDialog(MainView parent) {
        this.parent = parent;
    }

    public void show() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(msg.getString("countAdmin.title"));
        dialog.setHeaderText(msg.getString("countAdmin.header"));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField adminNameField = new TextField();
        DatePicker birthdayPicker = new DatePicker();
        TextField adminHeightField = new TextField();
        TextField adminPassportField = new TextField();

        grid.add(new Label(msg.getString("countAdmin.label.name")), 0, 0);
        grid.add(adminNameField, 1, 0);
        grid.add(new Label(msg.getString("countAdmin.label.birthday")), 0, 1);
        grid.add(birthdayPicker, 1, 1);
        grid.add(new Label(msg.getString("countAdmin.label.height")), 0, 2);
        grid.add(adminHeightField, 1, 2);
        grid.add(new Label(msg.getString("countAdmin.label.passport")), 0, 3);
        grid.add(adminPassportField, 1, 3);

        ButtonType countBtn = new ButtonType(msg.getString("countAdmin.button.count"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, countBtn);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == countBtn) {
                try {
                    String name   = adminNameField.getText().trim();
                    LocalDate date = birthdayPicker.getValue();

                    if (name.isEmpty()) {
                        throw new EmptyLine(msg.getString("countAdmin.error.nameEmpty"));
                    }
                    if (date == null) {
                        throw new EmptyLine(msg.getString("countAdmin.error.birthdayEmpty"));
                    }

                    LocalDateTime birthday = date.atStartOfDay();
                    if (birthday.isAfter(LocalDateTime.now())) {
                        throw new DataInTheFuture(msg.getString("countAdmin.error.futureDate"));
                    }

                    String heightTxt = adminHeightField.getText().trim();
                    Double height = heightTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                            msg.getString("countAdmin.label.height"), Double.class,
                            false, true, false,
                            heightTxt, false, null, false
                    );

                    String passport = adminPassportField.getText().trim();

                    Person admin = new Person(name, birthday, height, passport);
                    String response = ClientService.countByAdmin(admin);
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
