package gui;
import collection.FormOfEducation;
import collection.Semester;
import commands.Commands;
import exceptions.ServerDisconnect;
import io.Authentication;
import io.DistributionOfTheOutputStream;
import io.Server;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.util.Callback;
import service.ClientService;
import collection.StudyGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import service.Localization;
import storage.Logging;
import javafx.scene.control.TableView;
import storage.Request;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import static service.ClientService.showAllGroupsParsed;

public class MainView {
    private final Stage stage;
    private TableView<StudyGroup> tableView;
    private ObservableList<StudyGroup> dataList;
    private Timeline autoRefreshTimeline;
    private List<TableColumn<StudyGroup, ?>> savedSortOrder;
    private Dialog<Void> filterDialog;
    private TextField tfNameFilter, tfMinStudents, tfMaxStudents;
    private TextField tfMinX, tfMaxX, tfMinY, tfMaxY, tfAdminFilter;
    private final TabPane tabPane = new TabPane();
    private MapView mapView;
    private Tab graphTab, tableTab, mapTab;
    private VBox formCheckboxes, semCheckboxes;
    private List<CheckBox> formCheckBoxesList, semCheckBoxesList;
    private String nameFilter = "";
    private int minStudents = Integer.MIN_VALUE;
    private int maxStudents = Integer.MAX_VALUE;
    private double minX = Double.NEGATIVE_INFINITY;
    private double maxX = Double.POSITIVE_INFINITY;
    private double minY = Double.NEGATIVE_INFINITY;
    private double maxY = Double.POSITIVE_INFINITY;
    private String adminFilter = "";
    private List<FormOfEducation> selectedForms = new ArrayList<>();
    private List<Semester> selectedSems = new ArrayList<>();
    private ComboBox<Locale> localeCombo;

    public MainView(Stage stage) {
        this.stage = stage;
        initFilterDialog();
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
        ObservableList<Locale> locales = FXCollections.observableArrayList(
                new Locale("ru"),
                new Locale("et"),
                new Locale("lv"),
                new Locale("es", "MX")
        );
        localeCombo = new ComboBox<>(locales);
        localeCombo.setValue(Localization.getLocale());
        localeCombo = new ComboBox<>(locales);
        localeCombo.setValue(Localization.getLocale());

        Callback<ListView<Locale>, ListCell<Locale>> cellFactory = lv -> new ListCell<>() {
            private final ImageView img = new ImageView();
            @Override protected void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String code = item.getLanguage() + (item.getCountry().isEmpty() ? "" : "-" + item.getCountry());
                    Image flag = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/flags/" + code + ".png")), 24, 16, true, true);
                    img.setImage(flag);
                    String label = switch (code) {
                        case "ru" -> "Русский";
                        case "et" -> "Eesti";
                        case "lv" -> "Latviešu";
                        case "es-MX" -> "Español (MX)";
                        default -> item.getDisplayLanguage(item);
                    };
                    setText(label);
                    setGraphic(img);
                }
            }
        };
        localeCombo.setValue(Locale.ENGLISH);
        localeCombo.setCellFactory(cellFactory);
        localeCombo.setButtonCell(cellFactory.call(null));
        localeCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) Localization.setLocale(n);
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
        TableColumn<StudyGroup, Number> xCol = new TableColumn<>("Coord X");
        xCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getCoordinates().x() != null ? c.getValue().getCoordinates().x() : 0L
        ));
        xCol.setComparator(Comparator.comparingLong(Number::longValue));
        TableColumn<StudyGroup, Number> yCol = new TableColumn<>("Coord Y");
        yCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getCoordinates().y() != null ? c.getValue().getCoordinates().y() : 0f
        ));
        yCol.setComparator(Comparator.comparingDouble(Number::doubleValue));
        TableColumn<StudyGroup, Number> studentsCol = new TableColumn<>("Students");
        studentsCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStudentCount()));
        studentsCol.setComparator(Comparator.comparingInt(Number::intValue));
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
        this.mapView = new MapView(dataList, tableTab, tableView);
        mapTab = new Tab("Map", this.mapView);
        tabPane.getTabs().addAll(tableTab, graphTab, mapTab);
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
        Button execScriptBtn = new Button("Execute Script");
        Button updateBtn = new Button("Update by ID");
        Button removeBtn = new Button("Remove by ID");
        Button removeAdminBtn = new Button("Remove by Admin");
        Button clearBtn = new Button("Clear");
        Button countBtn = new Button("Count by Admin");
        Button exitBtn = new Button("Exit");
        Button helpBtn = new Button("Help");
        Button infoBtn = new Button("Info");
        Button filterBtn = new Button("Filters");
        buttonBox.getChildren().addAll(
                addBtn, updateBtn, removeBtn,
                removeAdminBtn, clearBtn, countBtn,
                exitBtn, helpBtn, infoBtn,
                execScriptBtn, filterBtn
        );

        root.setBottom(buttonBox);
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
        addBtn.setOnAction(e -> new AddDialog(this).show());
        updateBtn.setOnAction(e -> new UpdateDialog(this).show());
        removeBtn.setOnAction(e -> handleRemove());
        clearBtn.setOnAction(e -> handleClear());
        countBtn.setOnAction(e -> handleCount());
        execScriptBtn.setOnAction(e -> new ExecuteScriptDialog(this).show());
        exitBtn.setOnAction(e -> {
            stopAutoRefresh();
            stage.close();
        });
        helpBtn.setOnAction(e -> handleHelp());
        infoBtn.setOnAction(e -> handleInfo());
        filterBtn.setOnAction(e -> filterDialog.showAndWait());
        removeAdminBtn.setOnAction(e -> new RemoveByAdminDialog(this).show());
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
        HBox topBar = new HBox(10, localeCombo, userButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        root.setTop(topBar);
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

    public void handleRefresh() {
        new Thread(() -> {
            try {
                List<StudyGroup> list = ClientService.fetchAllGroups();
                Platform.runLater(() -> {
                    dataList.setAll(list);
                    applyStoredFilter();
                    if (graphTab.getContent() instanceof GraphView graph) {
                        try {
                            graph.refreshGraph(tableView.getItems(), tableTab, tableView);
                        } catch (ServerDisconnect e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (mapTab.getContent() instanceof MapView mv) {
                        mv.refreshMap(tableView.getItems());
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }


    private void initFilterDialog() {
        filterDialog = new Dialog<>();
        filterDialog.initModality(Modality.APPLICATION_MODAL);
        filterDialog.setTitle("Filter Settings");
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));
        tfNameFilter = new TextField(nameFilter);
        tfMinStudents = new TextField(minStudents == Integer.MIN_VALUE ? "" : String.valueOf(minStudents));
        tfMaxStudents = new TextField(maxStudents == Integer.MAX_VALUE ? "" : String.valueOf(maxStudents));
        tfMinX = new TextField(Double.isInfinite(minX) ? "" : String.valueOf(minX));
        tfMaxX = new TextField(Double.isInfinite(maxX) ? "" : String.valueOf(maxX));
        tfMinY = new TextField(Double.isInfinite(minY) ? "" : String.valueOf(minY));
        tfMaxY = new TextField(Double.isInfinite(maxY) ? "" : String.valueOf(maxY));
        tfAdminFilter = new TextField(adminFilter);
        formCheckboxes = new VBox(5);
        formCheckboxes.setAlignment(Pos.CENTER_LEFT);
        formCheckBoxesList = new ArrayList<>();
        for (FormOfEducation form : FormOfEducation.values()) {
            CheckBox cb = new CheckBox(form.name());
            cb.setSelected(selectedForms.contains(form));
            formCheckBoxesList.add(cb);
            formCheckboxes.getChildren().add(cb);
        }
        semCheckboxes = new VBox(5);
        semCheckboxes.setAlignment(Pos.CENTER_LEFT);
        semCheckBoxesList = new ArrayList<>();
        for (Semester sem : Semester.values()) {
            CheckBox cb = new CheckBox(sem.name());
            cb.setSelected(selectedSems.contains(sem));
            semCheckBoxesList.add(cb);
            semCheckboxes.getChildren().add(cb);
        }
        grid.add(new Label("Name contains:"), 0, 0); grid.add(tfNameFilter, 1, 0);
        grid.add(new Label("Min Students:"), 0, 1); grid.add(tfMinStudents, 1, 1);
        grid.add(new Label("Max Students:"), 0, 2); grid.add(tfMaxStudents, 1, 2);
        grid.add(new Label("Min X:"), 0, 3); grid.add(tfMinX, 1, 3);
        grid.add(new Label("Max X:"), 0, 4); grid.add(tfMaxX, 1, 4);
        grid.add(new Label("Min Y:"), 0, 5); grid.add(tfMinY, 1, 5);
        grid.add(new Label("Max Y:"), 0, 6); grid.add(tfMaxY, 1, 6);
        grid.add(new Label("Form of Education:"), 0, 7); grid.add(formCheckboxes, 1, 7);
        grid.add(new Label("Semester:"), 0, 8); grid.add(semCheckboxes, 1, 8);
        grid.add(new Label("Admin contains:"), 0, 9); grid.add(tfAdminFilter, 1, 9);
        ButtonType apply = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        ButtonType reset = new ButtonType("Reset", ButtonBar.ButtonData.OTHER);
        filterDialog.getDialogPane().getButtonTypes().addAll(apply, reset, ButtonType.CANCEL);
        filterDialog.getDialogPane().setContent(grid);
        filterDialog.setResultConverter(btn -> {
            if (btn == apply) saveCurrentFilters();
            else if (btn == reset) {
                resetFilters();
                saveCurrentFilters();
            }
            return null;
        });
    }

    private void saveCurrentFilters() {
        nameFilter = tfNameFilter.getText().trim().toLowerCase();
        minStudents = parseIntOr(tfMinStudents.getText(), Integer.MIN_VALUE);
        maxStudents = parseIntOr(tfMaxStudents.getText(), Integer.MAX_VALUE);
        minX = parseDoubleOr(tfMinX.getText(), Double.NEGATIVE_INFINITY);
        maxX = parseDoubleOr(tfMaxX.getText(), Double.POSITIVE_INFINITY);
        minY = parseDoubleOr(tfMinY.getText(), Double.NEGATIVE_INFINITY);
        maxY = parseDoubleOr(tfMaxY.getText(), Double.POSITIVE_INFINITY);
        adminFilter = tfAdminFilter.getText().trim().toLowerCase();
        selectedForms = formCheckBoxesList.stream().filter(CheckBox::isSelected).map(cb -> FormOfEducation.valueOf(cb.getText())).collect(Collectors.toList());
        selectedSems = semCheckBoxesList.stream().filter(CheckBox::isSelected).map(cb -> Semester.valueOf(cb.getText())).collect(Collectors.toList());
        applyStoredFilter();
    }

    private void applyStoredFilter() {
        List<StudyGroup> filtered = dataList.stream().filter(g -> {
            if (!nameFilter.isEmpty() && !g.getName().toLowerCase().contains(nameFilter)) return false;
            int s = g.getStudentCount();
            if (s < minStudents || s > maxStudents) return false;
            Long x = g.getCoordinates().x(); if (x != null && (x < minX || x > maxX)) return false;
            Float y = g.getCoordinates().y(); if (y != null && (y < minY || y > maxY)) return false;
            if (!selectedForms.isEmpty() && (g.getFormOfEducation() == null || !selectedForms.contains(g.getFormOfEducation()))) return false;
            if (!selectedSems.isEmpty() && (g.getSemester() == null || !selectedSems.contains(g.getSemester()))) return false;
            if (!adminFilter.isEmpty() && (g.getGroupAdmin() == null || !g.getGroupAdmin().name().toLowerCase().contains(adminFilter))) return false;
            return true;
        }).collect(Collectors.toList());
        tableView.getItems().setAll(filtered);
    }

    private void resetFilters() {
        nameFilter = "";
        minStudents = Integer.MIN_VALUE;
        maxStudents = Integer.MAX_VALUE;
        minX = Double.NEGATIVE_INFINITY;
        maxX = Double.POSITIVE_INFINITY;
        minY = Double.NEGATIVE_INFINITY;
        maxY = Double.POSITIVE_INFINITY;
        adminFilter = "";
        selectedForms.clear();
        selectedSems.clear();
        tfNameFilter.clear();
        tfMinStudents.clear();
        tfMaxStudents.clear();
        tfMinX.clear(); tfMaxX.clear();
        tfMinY.clear(); tfMaxY.clear();
        tfAdminFilter.clear();
        formCheckBoxesList.forEach(cb -> cb.setSelected(false));
        semCheckBoxesList.forEach(cb -> cb.setSelected(false));
        applyStoredFilter();
    }

    private int parseIntOr(String t, int def) {
        try { return Integer.parseInt(t.trim()); } catch(Exception e) { return def; }
    }

    private double parseDoubleOr(String t, double def) {
        try { return Double.parseDouble(t.trim()); } catch(Exception e) { return def; }
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
                String rawResponse = Server.interaction(new Request<>(Commands.HELP, null));
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
                String rawResponse = Server.interaction(new Request<>(Commands.INFO, null));
                String response = DistributionOfTheOutputStream.printFromServer(rawResponse);
                StringBuilder htmlContent = new StringBuilder("<html><body style='font-family: sans-serif;'>");
                for (String line : response.split("\n")) {
                    if (line.toLowerCase().contains("количество элементов") || line.toLowerCase().contains("elements")) {
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