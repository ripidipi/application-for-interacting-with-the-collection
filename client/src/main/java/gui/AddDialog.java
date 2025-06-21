package gui;

import commands.Commands;
import io.*;
import service.ClientService;
import collection.StudyGroup;
import collection.Coordinates;
import collection.Person;
import collection.FormOfEducation;
import collection.Semester;
import exceptions.EmptyLine;
import exceptions.DataInTheFuture;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.Localization;
import storage.Request;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddDialog {
    private final MainView parent;
    private final ResourceBundle bundle = Localization.bundle();

    public AddDialog(MainView parent) {
        this.parent = parent;
    }

    public void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(bundle.getString("adddialog.title"));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField xField = new TextField();
        TextField yField = new TextField();
        TextField studentsField = new TextField();
        ComboBox<FormOfEducation> formBox = new ComboBox<>(
                FXCollections.observableArrayList(FormOfEducation.values())
        );
        ComboBox<Semester> semBox = new ComboBox<>(
                FXCollections.observableArrayList(Semester.values())
        );
        TextField adminNameField = new TextField();
        DatePicker birthdayPicker = new DatePicker();
        TextField adminHeightField = new TextField();
        TextField adminPassportField = new TextField();
        CheckBox ifMaxCheck = new CheckBox(bundle.getString("adddialog.ifMax"));

        grid.add(new Label(bundle.getString("adddialog.label.name")), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label(bundle.getString("adddialog.label.coordX")), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label(bundle.getString("adddialog.label.coordY")), 0, 2);
        grid.add(yField, 1, 2);
        grid.add(new Label(bundle.getString("adddialog.label.students")), 0, 3);
        grid.add(studentsField, 1, 3);
        grid.add(new Label(bundle.getString("adddialog.label.form")), 0, 4);
        grid.add(formBox, 1, 4);
        grid.add(new Label(bundle.getString("adddialog.label.semester")), 0, 5);
        grid.add(semBox, 1, 5);
        grid.add(new Label(bundle.getString("adddialog.label.adminName")), 0, 6);
        grid.add(adminNameField, 1, 6);
        grid.add(new Label(bundle.getString("adddialog.label.birthday")), 0, 7);
        grid.add(birthdayPicker, 1, 7);
        grid.add(new Label(bundle.getString("adddialog.label.adminHeight")), 0, 8);
        grid.add(adminHeightField, 1, 8);
        grid.add(new Label(bundle.getString("adddialog.label.adminPassport")), 0, 9);
        grid.add(adminPassportField, 1, 9);
        grid.add(ifMaxCheck, 1, 10);

        Button addBtn = new Button(bundle.getString("adddialog.button.add"));
        grid.add(addBtn, 1, 11);

        dialog.setScene(new Scene(grid, 450, 520));
        dialog.show();

        addBtn.setOnAction(e -> {
            try {
                String nameTxt = nameField.getText().trim();
                String xTxt = xField.getText().trim();
                String yTxt = yField.getText().trim();
                String studentsTxt = studentsField.getText().trim();
                FormOfEducation formVal = formBox.getValue();
                String semTxt = semBox.getValue() != null ? semBox.getValue().name() : "";
                String admNameTxt = adminNameField.getText().trim();
                LocalDate date = birthdayPicker.getValue();
                String heightTxt = adminHeightField.getText().trim();
                String passTxt = adminPassportField.getText().trim();

                String name = PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.name"), String.class, true, false, false,
                        nameTxt, false, null, false
                );

                Long x = xTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.coordX"), Long.class, false, false, false,
                        xTxt, false, null, false
                );

                Float y = yTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.coordY"), Float.class, false, false, false,
                        yTxt, false, null, false
                );

                Integer students = PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.students"), Integer.class, true, true, false,
                        studentsTxt, false, null, false
                );

                FormOfEducation form = EnumTransform.TransformToEnum(
                        FormOfEducation.class,
                        formVal != null ? formVal.name() : ""
                );
                Semester sem = EnumTransform.TransformToEnum(Semester.class, semTxt);

                String adminName = PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.adminName"), String.class, true, false, false,
                        admNameTxt, false, null, false
                );

                if (date == null) throw new EmptyLine(bundle.getString("adddialog.key.birthday"));
                LocalDateTime adminBirthday = date.atStartOfDay();
                if (adminBirthday.isAfter(LocalDateTime.now()))
                    throw new DataInTheFuture(bundle.getString("adddialog.key.birthday"));

                Double height = heightTxt.isEmpty()
                        ? null
                        : PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.adminHeight"), Double.class, false, true, false,
                        heightTxt, false, null, false
                );

                String passport = PrimitiveDataTransform.transformToRequiredType(
                        bundle.getString("adddialog.key.adminPassport"), String.class, true, false, false,
                        passTxt, false, null, false
                );

                Coordinates coords = new Coordinates(x, y);
                Person admin = new Person(adminName, adminBirthday, height, passport);
                StudyGroup group = new StudyGroup(
                        name, coords, students, form, sem, admin,
                        Authentication.getInstance().getUsername()
                );

                String response;
                if (ifMaxCheck.isSelected()) {
                    Request<StudyGroup> req = new Request<>(Commands.ADD_IF_MAX, group);
                    response = Server.interaction(req);
                } else {
                    response = ClientService.addGroup(group);
                }
                new Alert(Alert.AlertType.INFORMATION,
                        DistributionOfTheOutputStream.printFromServer(response), ButtonType.OK
                ).showAndWait();
                dialog.close();
                parent.handleRefresh();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        ex.getMessage(), ButtonType.OK
                ).showAndWait();
            }
        });
    }
}
