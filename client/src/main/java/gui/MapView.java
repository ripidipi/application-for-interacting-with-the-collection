package gui;

import collection.FormOfEducation;
import collection.Semester;
import collection.StudyGroup;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MapView extends StackPane {
    private final ImageView background;
    private final Group mapGroup = new Group();
    private final List<StudyGroup> groups;
    private final Tab tableTab;
    private final TableView<StudyGroup> tableView;
    private final Map<FormOfEducation, Image> iconMap = new EnumMap<>(FormOfEducation.class);
    private double rangeX = 1, rangeY = 1;

    public MapView(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) {
        this.groups = new ArrayList<>(groups);
        this.tableTab = tableTab;
        this.tableView = tableView;
        loadIcons();
        background = createBackground();

        Pane overlay = new Pane(mapGroup);
        overlay.setPickOnBounds(false);
        enableZoomAndPan(overlay);

        getChildren().addAll(background, overlay);
        setPadding(new Insets(10));

        calculateRanges();
        drawPoints();
    }

    private void loadIcons() {
        iconMap.put(FormOfEducation.FULL_TIME_EDUCATION, load("/images/full_time.png"));
        iconMap.put(FormOfEducation.DISTANCE_EDUCATION, load("/images/distance.png"));
        iconMap.put(FormOfEducation.EVENING_CLASSES, load("/images/evening.png"));
    }

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    private ImageView createBackground() {
        ImageView view = new ImageView(load("/images/map.jpg"));
        view.setPreserveRatio(true);
        view.setFitWidth(900);
        view.setFitHeight(650);
        view.setEffect(new DropShadow(10, Color.gray(0, 0.4)));
        return view;
    }

    private void enableZoomAndPan(Pane pane) {
        pane.setOnScroll((ScrollEvent e) -> {
            double factor = e.getDeltaY() > 0 ? 1.15 : 0.85;
            pane.setScaleX(pane.getScaleX() * factor);
            pane.setScaleY(pane.getScaleY() * factor);
            e.consume();
        });
        final Delta drag = new Delta();
        pane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                drag.x = e.getSceneX(); drag.y = e.getSceneY();
            }
        });
        pane.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                pane.setTranslateX(pane.getTranslateX() + e.getSceneX() - drag.x);
                pane.setTranslateY(pane.getTranslateY() + e.getSceneY() - drag.y);
                drag.x = e.getSceneX(); drag.y = e.getSceneY();
            }
        });
    }

    private static class Delta { double x, y; }

    private void calculateRanges() {
        rangeX = 1; rangeY = 1;
        for (StudyGroup g : groups) {
            if (g.getCoordinates() == null) continue;
            Long x = g.getCoordinates().x(); Float y = g.getCoordinates().y();
            if (x != null) rangeX = Math.max(rangeX, Math.abs(x));
            if (y != null) rangeY = Math.max(rangeY, Math.abs(y));
        }

        rangeX *= 0.4;
        rangeY *= 0.1;
    }

    public void refreshMap(List<StudyGroup> newGroups) {
        groups.clear(); groups.addAll(newGroups);
        calculateRanges(); drawPoints();
    }

    private void drawPoints() {
        mapGroup.getChildren().clear();
        Bounds b = background.getBoundsInParent();
        if (b.isEmpty()) return;
        double w = b.getWidth(), h = b.getHeight();
        double cx = w / 2, cy = h / 2;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (StudyGroup g : groups) {
            if (g.getCoordinates() == null) continue;
            Long gx = g.getCoordinates().x(); Float gy = g.getCoordinates().y();
            if (gx == null || gy == null) continue;

            double px = cx + (gx / rangeX) * (w / 1.5);
            double py = cy - (gy / rangeY) * (h / 1.2);

            double radius = Math.min(15, 4 + Math.log(g.getStudentCount() + 1) * 1.5);
            Circle circle = new Circle(px, py, radius);
            circle.setFill(getColorByForm(g.getFormOfEducation()));
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1);
            circle.setEffect(new DropShadow(4, Color.gray(0, 0.25)));

            ImageView iv = new ImageView(iconMap.get(g.getFormOfEducation()));
            iv.setFitWidth(radius);
            iv.setFitHeight(radius);
            iv.setTranslateX(px - radius * 0.5);
            iv.setTranslateY(py - radius * 0.5);

            Group grp = new Group(circle, iv);
            FadeTransition ft = new FadeTransition(Duration.millis(300), grp);
            ft.setFromValue(0); ft.setToValue(1); ft.play();

            Tooltip tip = new Tooltip(
                    "ID: " + g.getId() + "\n" +
                            g.getName() + "\nStudents: " + g.getStudentCount() +
                            "\nForm: " + g.getFormOfEducation() +
                            "\nSemester: " + g.getSemester() +
                            "\nDate: " + g.getGroupAdmin().birthday().format(fmt)
            );
            Tooltip.install(grp, tip);

            grp.setOnMouseClicked(e -> {
                tableView.getSelectionModel().select(g);
                tableView.scrollTo(g);
                tableTab.getTabPane().getSelectionModel().select(tableTab);
            });

            mapGroup.getChildren().add(grp);
        }
    }

    private Color getColorByForm(FormOfEducation f) {
        return switch (f) {
            case FULL_TIME_EDUCATION -> Color.web("#4E79A7");
            case DISTANCE_EDUCATION -> Color.web("#F28E2B");
            case EVENING_CLASSES -> Color.web("#B07AA1");
        };
    }
}