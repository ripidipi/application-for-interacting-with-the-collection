package gui;

import collection.FormOfEducation;
import collection.Semester;
import collection.StudyGroup;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import java.util.List;
import java.util.ArrayList;

public class MapView extends StackPane {
    private final ImageView background;
    private final Group mapGroup = new Group();
    private final List<StudyGroup> groups;
    private final TableView<StudyGroup> tableView;
    private final Tab tableTab;

    private double minX, maxX, minY, maxY;

    public MapView(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) {
        this.groups = new ArrayList<>(groups);
        this.tableTab = tableTab;
        this.tableView = tableView;

        Image img = new Image(getClass().getResourceAsStream("/images/map.jpg"));
        background = new ImageView(img);
        background.setPreserveRatio(true);
        background.setFitWidth(800);
        background.setFitHeight(600);

        getChildren().addAll(background, mapGroup);
        calculateBounds();
        drawPoints();
    }

    private void calculateBounds() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;

        for (StudyGroup g : groups) {
            if (g.getCoordinates() == null) continue;
            Long x = g.getCoordinates().x();
            Float y = g.getCoordinates().y();
            if (x == null || y == null) continue;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        if (minX == Double.MAX_VALUE) minX = 0;
        if (minY == Double.MAX_VALUE) minY = 0;
        if (maxX == Double.MIN_VALUE) maxX = 0;
        if (maxY == Double.MIN_VALUE) maxY = 0;
        // Avoid zero-size
        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }
    }

    public void refreshMap(List<StudyGroup> newGroups) {
        groups.clear();
        groups.addAll(newGroups);
        calculateBounds();
        drawPoints();
    }

    private void drawPoints() {
        mapGroup.getChildren().clear();
        if (groups.isEmpty()) return;

        Bounds b = background.getBoundsInParent();
        if (b.isEmpty()) return;

        double width = b.getWidth();
        double height = b.getHeight();
        double scaleX = width  / (maxX - minX);
        double scaleY = height / (maxY - minY);
        double scale = Math.min(scaleX, scaleY);

        for (StudyGroup group : groups) {
            if (group.getCoordinates() == null) continue;
            Long x = group.getCoordinates().x();
            Float y = group.getCoordinates().y();
            if (x == null || y == null) continue;

            double pixelX = b.getMinX() + (x - minX) * scale;
            double pixelY = b.getMinY() + (maxY - y) * scale;

            double radius = Math.max(8, Math.log10(group.getStudentCount() + 1) * 6);
            Color color = getColorByFormAndSemester(group.getFormOfEducation(), group.getSemester());

            Circle circle = new Circle(pixelX, pixelY, radius, color);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1);
            circle.setOpacity(0.85);

            Text label = new Text(group.getId().toString());
            label.setFont(Font.font(10));
            label.setX(pixelX - radius/2);
            label.setY(pixelY + radius/2);

            circle.setOnMouseClicked(e -> {
                tableView.getSelectionModel().select(group);
                tableView.scrollTo(group);
                tableTab.getTabPane().getSelectionModel().select(tableTab);
            });

            mapGroup.getChildren().addAll(circle, label);
        }
    }

    private Color getColorByFormAndSemester(FormOfEducation form, Semester semester) {
        if (form == null || semester == null) return Color.LIGHTGRAY;
        return switch (form) {
            case FULL_TIME_EDUCATION -> switch (semester) {
                case THIRD -> Color.web("#4E79A7");
                case FOURTH -> Color.web("#A0CBE8");
                case FIFTH -> Color.web("#76B7B2");
                case SEVENTH -> Color.web("#59A14F");
                case EIGHTH -> Color.web("#8CD17D");
            };
            case DISTANCE_EDUCATION -> switch (semester) {
                case THIRD -> Color.web("#F28E2B");
                case FOURTH -> Color.web("#FFBE7D");
                case FIFTH -> Color.web("#E15759");
                case SEVENTH -> Color.web("#FF9D9A");
                case EIGHTH -> Color.web("#B6992D");
            };
            case EVENING_CLASSES -> switch (semester) {
                case THIRD -> Color.web("#B07AA1");
                case FOURTH -> Color.web("#D4A6C8");
                case FIFTH -> Color.web("#9D7660");
                case SEVENTH -> Color.web("#D7B5A6");
                case EIGHTH -> Color.web("#499894");
            };
        };
    }
}
