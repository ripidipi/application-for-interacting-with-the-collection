package gui;

import io.DistributionOfTheOutputStream;
import javafx.beans.property.SimpleStringProperty;
import service.ClientService;
import collection.StudyGroup;
import collection.Coordinates;
import collection.Person;
import collection.FormOfEducation;
import collection.Semester;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main application view displaying StudyGroup table with auto-refresh.
 */
public class MainView {
    private final Stage stage;
    private TableView<StudyGroup> tableView;
    private ObservableList<StudyGroup> dataList;
    private Timeline autoRefreshTimeline;
    private List<TableColumn<StudyGroup, ?>> savedSortOrder;


    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("StudyGroup Collection");
        BorderPane root = new BorderPane();

        tableView = new TableView<>();
        dataList = FXCollections.observableArrayList();
        tableView.setItems(dataList);


        TableColumn<StudyGroup, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StudyGroup, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StudyGroup, String> xCol = new TableColumn<>("Coord X");
        xCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getCoordinates().xToString())
        );

        TableColumn<StudyGroup, String> yCol = new TableColumn<>("Coord Y");
        yCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getCoordinates().yToString())
        );

        TableColumn<StudyGroup, Integer> studentsCol = new TableColumn<>("Students");
        studentsCol.setCellValueFactory(new PropertyValueFactory<>("studentCount"));

        TableColumn<StudyGroup, String> formCol = new TableColumn<>("Form");
        formCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        Optional.ofNullable(cell.getValue().getFormOfEducation())
                                .map(Enum::name)
                                .orElse("")
                )
        );

        TableColumn<StudyGroup, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        Optional.ofNullable(cell.getValue().getSemester())
                                .map(Enum::name)
                                .orElse("")
                )
        );

        TableColumn<StudyGroup, String> adminNameCol = new TableColumn<>("Admin Name");
        adminNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGroupAdmin().name()));

        TableColumn<StudyGroup, String> adminBirthdayCol = new TableColumn<>("Admin Birthday");
        adminBirthdayCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getGroupAdmin().birthday().toString()
        ));

        TableColumn<StudyGroup, String> adminHeightCol = new TableColumn<>("Admin Height");
        adminHeightCol.setCellValueFactory(data -> {
            Double height = data.getValue().getGroupAdmin().height();
            return new SimpleStringProperty(height != null ? height.toString() : "");
        });

        TableColumn<StudyGroup, String> adminPassportCol = new TableColumn<>("Admin Passport");
        adminPassportCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGroupAdmin().passportID()));


        tableView.getColumns().addAll(
                idCol, nameCol, xCol, yCol,
                studentsCol, formCol, semCol, adminNameCol,
                adminBirthdayCol, adminHeightCol, adminPassportCol
        );

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        Button addBtn    = new Button("Add");
        Button updateBtn = new Button("Update by ID");
        Button removeBtn = new Button("Remove by ID");
        Button clearBtn  = new Button("Clear");
        Button countBtn  = new Button("Count by Admin");
        Button exitBtn   = new Button("Exit");
        buttonBox.getChildren().addAll(
                addBtn, updateBtn, removeBtn,
                clearBtn, countBtn, exitBtn
        );

        root.setCenter(tableView);
        root.setBottom(buttonBox);

        stage.setScene(new Scene(root, 1000, 600));
        stage.show();

        addBtn.setOnAction(e -> new AddDialog(this).show());
        updateBtn.setOnAction(e -> new UpdateDialog(this).show());
        removeBtn.setOnAction(e -> handleRemove());
        clearBtn.setOnAction(e -> handleClear());
        countBtn.setOnAction(e -> handleCount());
        exitBtn.setOnAction(e -> {
            stopAutoRefresh();
            stage.close();
        });

        handleRefresh();
        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), ev -> handleRefresh())
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
    }

    void handleRefresh() {
        new Thread(() -> {
            try {
                List<StudyGroup> list = ClientService.fetchAllGroups();
                List<StudyGroup> filtered = list.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                savedSortOrder = new ArrayList<>(tableView.getSortOrder());
                Platform.runLater(() -> {
                    dataList.setAll(filtered);
                    tableView.getSortOrder().setAll(savedSortOrder);
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR, "Failed to refresh data: " + ex.getMessage(), ButtonType.OK).showAndWait()
                );
                ex.printStackTrace();
            }
        }).start();
    }

    private void handleRemove() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove by ID");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter ID:");
        dialog.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr.trim());
                String resp = ClientService.removeById(id);
                new Alert(Alert.AlertType.INFORMATION, DistributionOfTheOutputStream.printFromServer(resp), ButtonType.OK).showAndWait();
                handleRefresh();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void handleClear() {
        try {
            String resp = ClientService.clearAll();
            new Alert(Alert.AlertType.INFORMATION, DistributionOfTheOutputStream.printFromServer(resp), ButtonType.OK).showAndWait();
            handleRefresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void handleCount() {
        new CountByAdminDialog(this).show();
    }
}
