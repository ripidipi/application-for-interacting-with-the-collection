package gui;

import commands.Commands;
import exceptions.EmptyLine;
import exceptions.ZeroValue;
import exceptions.RemoveOfTheNextSymbol;
import exceptions.IncorrectValue;
import exceptions.DataInTheFuture;
import io.Authentication;
import io.DistributionOfTheOutputStream;
import io.PrimitiveDataTransform;
import io.EnumTransform;
import io.Server;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ClientService;
import collection.StudyGroup;
import collection.Coordinates;
import collection.Person;
import collection.FormOfEducation;
import collection.Semester;
import storage.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static exceptions.CheckObjWithId.checkObjWithId;

public class UpdateDialog {
    private final MainView parent;
    private final String preFilledId;

    public UpdateDialog(MainView parent) {
        this(parent, null);
    }

    public UpdateDialog(MainView parent, String preFilledId) {
        this.parent = parent;
        this.preFilledId = preFilledId;
    }

    public void show() {
        if (preFilledId != null) {
            new Thread(() -> {
                try {
                    Integer id = PrimitiveDataTransform.transformToRequiredType(
                            "id", Integer.class, true, true, false,
                            preFilledId.trim(), false, null, false
                    );
                    List<StudyGroup> groups = ClientService.fetchAllGroups();
                    Optional<StudyGroup> opt = groups.stream()
                            .filter(g -> id.equals(g.getId()))
                            .findFirst();
                    if (opt.isEmpty()) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                                "Группа с ID=" + id + " не найдена", ButtonType.OK)
                                .showAndWait());
                        return;
                    }
                    StudyGroup existing = opt.get();
                    Platform.runLater(() -> createUpdateDialog(id, existing));
                } catch (Exception ex) {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                            "Ошибка получения данных: " + ex.getMessage(), ButtonType.OK)
                            .showAndWait());
                }
            }).start();
        } else {
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("Update by ID");
            idDialog.setHeaderText(null);
            idDialog.setContentText("Enter ID:");
            idDialog.showAndWait().ifPresent(idStr -> new UpdateDialog(parent, idStr).show());
        }
    }

    private void createUpdateDialog(Integer id, StudyGroup group) {
        try {
            checkObjWithId(id);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Update StudyGroup");

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nameField = new TextField(group.getName());
            TextField xField = new TextField(
                    group.getCoordinates().x() != null ? group.getCoordinates().x().toString() : ""
            );
            TextField yField = new TextField(
                    group.getCoordinates().y() != null ? group.getCoordinates().y().toString() : ""
            );
            TextField studentsField = new TextField(
                    group.getStudentCount() != null ? group.getStudentCount().toString() : ""
            );

            ComboBox<FormOfEducation> formBox = new ComboBox<>(
                    FXCollections.observableArrayList(FormOfEducation.values())
            );
            formBox.setValue(group.getFormOfEducation());

            ComboBox<Semester> semBox = new ComboBox<>(
                    FXCollections.observableArrayList(Semester.values())
            );
            semBox.setValue(group.getSemester());

            TextField adminNameField = new TextField(group.getGroupAdmin().name());
            DatePicker birthdayPicker = new DatePicker(group.getGroupAdmin().birthday().toLocalDate());
            TextField adminHeightField = new TextField(
                    group.getGroupAdmin().height() != null ? group.getGroupAdmin().height().toString() : ""
            );
            TextField adminPassportField = new TextField(group.getGroupAdmin().passportID());

            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Coord X (optional):"), 0, 1);
            grid.add(xField, 1, 1);
            grid.add(new Label("Coord Y (optional):"), 0, 2);
            grid.add(yField, 1, 2);
            grid.add(new Label("Students Count:"), 0, 3);
            grid.add(studentsField, 1, 3);
            grid.add(new Label("Form Of Education:"), 0, 4);
            grid.add(formBox, 1, 4);
            grid.add(new Label("Semester:"), 0, 5);
            grid.add(semBox, 1, 5);
            grid.add(new Label("Admin Name:"), 0, 6);
            grid.add(adminNameField, 1, 6);
            grid.add(new Label("Admin Birthday:"), 0, 7);
            grid.add(birthdayPicker, 1, 7);
            grid.add(new Label("Admin Height (optional):"), 0, 8);
            grid.add(adminHeightField, 1, 8);
            grid.add(new Label("Admin PassportID:"), 0, 9);
            grid.add(adminPassportField, 1, 9);

            Button updateBtn = new Button("Update");
            grid.add(updateBtn, 1, 10);

            dialog.setScene(new Scene(grid, 450, 520));
            dialog.show();

            updateBtn.setOnAction(ev -> {
                try {
                    String nameTxt = nameField.getText().trim();
                    String xTxt = xField.getText().trim();
                    String yTxt = yField.getText().trim();
                    String studentsTxt = studentsField.getText().trim();
                    FormOfEducation form = EnumTransform.TransformToEnum(
                            FormOfEducation.class,
                            formBox.getValue() != null ? formBox.getValue().name() : ""
                    );
                    Semester sem = EnumTransform.TransformToEnum(
                            Semester.class,
                            semBox.getValue() != null ? semBox.getValue().name() : ""
                    );
                    String admNameTxt = adminNameField.getText().trim();
                    LocalDate date = birthdayPicker.getValue();
                    String heightTxt = adminHeightField.getText().trim();
                    String passTxt = adminPassportField.getText().trim();

                    String name = PrimitiveDataTransform.transformToRequiredType(
                            "name", String.class, true, false, false,
                            nameTxt, false, null, false
                    );
                    Long x = xTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                            "x coordinate", Long.class, false, false, false,
                            xTxt, false, null, false
                    );
                    Float y = yTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                            "y coordinate", Float.class, false, false, false,
                            yTxt, false, null, false
                    );
                    Integer students = PrimitiveDataTransform.transformToRequiredType(
                            "students count", Integer.class, true, true, false,
                            studentsTxt, false, null, false
                    );
                    String adminName = PrimitiveDataTransform.transformToRequiredType(
                            "admin name", String.class, true, false, false,
                            admNameTxt, false, null, false
                    );
                    if (date == null) throw new EmptyLine("admin birthday");
                    LocalDateTime adminBirthday = date.atStartOfDay();
                    if (adminBirthday.isAfter(LocalDateTime.now())) throw new DataInTheFuture("admin birthday");
                    Double height = heightTxt.isEmpty() ? null : PrimitiveDataTransform.transformToRequiredType(
                            "admin height", Double.class, false, true, false,
                            heightTxt, false, null, false
                    );
                    String passport = PrimitiveDataTransform.transformToRequiredType(
                            "admin passportID", String.class, true, false, false,
                            passTxt, false, null, false
                    );

                    Coordinates coords = new Coordinates(x, y);
                    Person admin = new Person(adminName, adminBirthday, height, passport);
                    StudyGroup newGroup = new StudyGroup(
                            id, name, coords, students,
                            form, sem, admin,
                            Authentication.getInstance().getUsername()
                    );

                    String response = ClientService.updateGroup(id, newGroup);
                    new Alert(Alert.AlertType.INFORMATION,
                            DistributionOfTheOutputStream.printFromServer(response),
                            ButtonType.OK).showAndWait();
                    dialog.close();
                    parent.handleRefresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        } catch (EmptyLine | ZeroValue | RemoveOfTheNextSymbol |
                 IncorrectValue | DataInTheFuture ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
