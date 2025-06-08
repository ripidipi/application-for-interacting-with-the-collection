package gui;

import commands.Commands;
import exceptions.ServerDisconnect;
import io.Authentication;
import io.DistributionOfTheOutputStream;
import io.Server;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import service.ClientService;
import collection.StudyGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import storage.Logging;
import javafx.scene.control.TableView;
import collection.StudyGroup;
import service.ClientService;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import storage.Request;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static service.ClientService.showAllGroupsParsed;

public class MainView {
    private final Stage stage;
    private TableView<StudyGroup> tableView;
    private ObservableList<StudyGroup> dataList;
    private Timeline autoRefreshTimeline;
    private List<TableColumn<StudyGroup, ?>> savedSortOrder;
    private final TabPane tabPane = new TabPane();
    private Tab graphTab;
    private Tab tableTab;

    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() throws Exception {
        stage.setTitle("StudyGroup Collection");
        BorderPane root = new BorderPane();
        tableView = new TableView<>();
        dataList = FXCollections.observableArrayList();
        tableView.setItems(dataList);


        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                StudyGroup selectedGroup = tableView.getSelectionModel().getSelectedItem();
                if (selectedGroup != null) {
                    try {
                        String permissionResponse = Server.interaction(
                                new Request<>(Commands.CHECK_IS_WITH_ID, selectedGroup.getId()));
                        if (!permissionResponse.contains("false")) {
                            Platform.runLater(() -> new UpdateDialog(this, selectedGroup.getId().toString()).show());
                        }
                    } catch (ServerDisconnect e) {
                        Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Permission check failed").showAndWait());
                    }
                }
            }
        });


        String currentUser = Authentication.getInstance().getUsername();

        tableView.setRowFactory(tv -> new TableRow<StudyGroup>() {
            @Override
            protected void updateItem(StudyGroup item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }
                try {
                    if (!Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID, item.getId())).contains("false")) {
                        setStyle("-fx-background-color: #e0e0e0;");
                    } else {
                        setStyle("");
                    }
                } catch (ServerDisconnect e) {
                    throw new RuntimeException(e);
                }
            }
        });


        TableColumn<StudyGroup, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getId() != null ? cell.getValue().getId().toString() : ""
        ));

        TableColumn<StudyGroup, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getName() != null ? cell.getValue().getName() : ""
        ));

        TableColumn<StudyGroup, String> xCol = new TableColumn<>("Coord X");
        xCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCoordinates() != null && cell.getValue().getCoordinates().x() != null
                        ? cell.getValue().getCoordinates().x().toString()
                        : ""
        ));

        TableColumn<StudyGroup, String> yCol = new TableColumn<>("Coord Y");
        yCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCoordinates() != null && cell.getValue().getCoordinates().y() != null
                        ? cell.getValue().getCoordinates().y().toString()
                        : ""
        ));

        TableColumn<StudyGroup, String> studentsCol = new TableColumn<>("Students");
        studentsCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getStudentCount() != null ? cell.getValue().getStudentCount().toString() : ""
        ));

        TableColumn<StudyGroup, String> formCol = new TableColumn<>("Form");
        formCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFormOfEducation() != null ? cell.getValue().getFormOfEducation().name() : ""
        ));

        TableColumn<StudyGroup, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getSemester() != null ? cell.getValue().getSemester().name() : ""
        ));

        TableColumn<StudyGroup, String> adminNameCol = new TableColumn<>("Admin Name");
        adminNameCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getGroupAdmin() != null && cell.getValue().getGroupAdmin().name() != null
                        ? cell.getValue().getGroupAdmin().name()
                        : ""
        ));

        TableColumn<StudyGroup, String> adminBirthdayCol = new TableColumn<>("Admin Birthday");
        adminBirthdayCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getGroupAdmin() != null && cell.getValue().getGroupAdmin().birthday() != null
                        ? cell.getValue().getGroupAdmin().birthday().format(DateTimeFormatter.ISO_DATE)
                        : ""
        ));

        TableColumn<StudyGroup, String> adminHeightCol = new TableColumn<>("Admin Height");
        adminHeightCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getGroupAdmin() != null && cell.getValue().getGroupAdmin().height() != null
                        ? cell.getValue().getGroupAdmin().height().toString()
                        : ""
        ));

        TableColumn<StudyGroup, String> adminPassportCol = new TableColumn<>("Admin Passport");
        adminPassportCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getGroupAdmin() != null && cell.getValue().getGroupAdmin().passportID() != null
                        ? cell.getValue().getGroupAdmin().passportID()
                        : ""
        ));


        tableView.getColumns().addAll(
                idCol, nameCol, xCol, yCol,
                studentsCol, formCol, semCol, adminNameCol,
                adminBirthdayCol, adminHeightCol, adminPassportCol
        );

        tableTab = new Tab("Table", tableView);
        List<StudyGroup> groups = showAllGroupsParsed();
        graphTab = new Tab("Graph", new GraphView(groups, tableTab, tableView));

        tabPane.getTabs().addAll(tableTab, graphTab);
        root.setCenter(tabPane);

        Button userButton = new Button(currentUser);
        userButton.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-font-weight: bold;");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem stayItem = new MenuItem("Stay");
        MenuItem logoutItem = new MenuItem("Leave");
        contextMenu.getItems().addAll(stayItem, logoutItem);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update by ID");
        Button removeBtn = new Button("Remove by ID");
        Button clearBtn = new Button("Clear");
        Button countBtn = new Button("Count by Admin");
        Button exitBtn = new Button("Exit");
        Button helpBtn = new Button("Help");
        Button infoBtn = new Button("Info");
        buttonBox.getChildren().addAll(
                addBtn, updateBtn, removeBtn,
                clearBtn, countBtn, exitBtn,
                helpBtn, infoBtn
        );

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
        helpBtn.setOnAction(e -> handleHelp());
        infoBtn.setOnAction(e -> handleInfo());
        userButton.setOnAction(e -> contextMenu.show(userButton, Side.BOTTOM, 0, 0));
        logoutItem.setOnAction(e -> {
            try {
                Authentication.logout();

                Platform.runLater(() -> {
                    stopAutoRefresh();
                    stage.close();
                    new LoginView(new Stage()).show();
                });
            } catch (Exception ex) {
                Logging.log(Logging.makeMessage(ex.getMessage(), ex.getStackTrace()));
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                        "problem to leave " + ex.getMessage()).showAndWait());
            }
        });


        HBox topRightBox = new HBox(userButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        root.setTop(topRightBox);
        handleRefresh();

        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), ev -> handleRefresh())
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
                    if (filtered.isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "No data received from server.",
                                ButtonType.OK).showAndWait();
                    }
                    tableView.getSortOrder().setAll(savedSortOrder);

                    if (graphTab.getContent() instanceof GraphView graph) {
                        try {
                            graph.refreshGraph(filtered, tableTab, tableView);
                        } catch (ServerDisconnect e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR, "Failed to refresh data: " +
                                ex.getMessage(), ButtonType.OK).showAndWait()
                );
                Logging.log(Logging.makeMessage(ex.getMessage(), ex.getStackTrace()));
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
                new Alert(Alert.AlertType.INFORMATION, DistributionOfTheOutputStream.printFromServer(resp),
                        ButtonType.OK).showAndWait();
                handleRefresh();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void handleClear() {
        try {
            String resp = ClientService.clearAll();
            new Alert(Alert.AlertType.INFORMATION, DistributionOfTheOutputStream.printFromServer(resp),
                    ButtonType.OK).showAndWait();
            handleRefresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void handleCount() {
        new CountByAdminDialog(this).show();
    }

    private void handleHelp() {
        new Thread(() -> {
            try {
                String rawResponse = Server.interaction(new storage.Request<>(commands.Commands.HELP, null));
                String response = DistributionOfTheOutputStream.printFromServer(rawResponse);

                StringBuilder htmlContent = new StringBuilder("<html><body style='font-family: sans-serif;'>");

                Arrays.stream(response.split("\n")).forEach(line -> {
                    if (line.matches("^\\s*\\w+.*")) {
                        String cmd = line.split(" ")[0];
                        htmlContent.append("<b>").append(cmd).append("</b>").append(line.substring(cmd.length()));
                    } else {
                        htmlContent.append(line);
                    }
                    htmlContent.append("<br>");
                });

                htmlContent.append("</body></html>");

                Platform.runLater(() -> showHtmlDialog("Help", htmlContent.toString()));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Failed to fetch help: " + ex.getMessage()));
            }
        }).start();
    }

    private void handleInfo() {
        new Thread(() -> {
            try {
                String rawResponse = Server.interaction(new storage.Request<>(commands.Commands.INFO, null));
                String response = DistributionOfTheOutputStream.printFromServer(rawResponse);

                StringBuilder htmlContent = new StringBuilder("<html><body style='font-family: sans-serif;'>");

                for (String line : response.split("\n")) {
                    if (line.toLowerCase().contains("количество") || line.toLowerCase().contains("elements")) {
                        htmlContent.append("<div style='font-weight: bold; font-size: 1.2em;'>").append(line).append("</div>");
                    } else {
                        htmlContent.append("<div>").append(line).append("</div>");
                    }
                }

                htmlContent.append("</body></html>");

                String finalHtml = htmlContent.toString();

                Platform.runLater(() -> showMiniHtmlDialog(finalHtml));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Failed to fetch info: " + ex.getMessage()));
            }
        }).start();
    }



    private void showHtmlDialog(String title, String htmlContent) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        WebView webView = new WebView();
        webView.getEngine().loadContent(htmlContent);

        VBox root = new VBox(webView);
        root.setPadding(new Insets(10));
        root.setPrefSize(600, 400);

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showMiniHtmlDialog(String htmlContent) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Info");

        WebView webView = new WebView();
        webView.setPrefSize(300, 100);
        webView.getEngine().loadContent(htmlContent);

        VBox root = new VBox(webView);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }


    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }


}