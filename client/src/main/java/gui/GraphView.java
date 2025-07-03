package gui;

import collection.StudyGroup;
import commands.Commands;
import exceptions.ServerDisconnect;
import io.Server;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import storage.Request;
import java.util.ArrayList;
import java.util.List;

public class GraphView extends Pane {
    public enum LayoutMode { SCATTERED, CIRCULAR }

    private final Group graphContainer = new Group();
    private double scale = 1.0;
    private List<StudyGroup> groups;
    private final List<Circle> nodes = new ArrayList<>();
    private final List<Line> edges = new ArrayList<>();
    private LayoutMode currentLayoutMode = LayoutMode.SCATTERED;
    private MainView parent;
    private Tab tableTab;
    private TableView<StudyGroup> tableView;
    private final List<double[]> positions = new ArrayList<>();
    private static final int ITERATIONS = 500;
    private static final double AREA_MULTIPLIER = 50.0;
    private static final double COOLING_FACTOR = 0.95;

    public GraphView(List<StudyGroup> groups, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        this.groups = groups;
        this.parent = parent;
        this.tableTab = tableTab;
        this.tableView = tableView;
        getChildren().add(graphContainer);
        initZoomAndDrag();
        widthProperty().addListener((obs, o, n) -> refreshGraph(groups, parent, tableTab, tableView));
        heightProperty().addListener((obs, o, n) -> refreshGraph(groups, parent, tableTab, tableView));
        drawGraph(groups, parent, tableTab, tableView);
    }

    public void setLayoutMode(LayoutMode mode) {
        this.currentLayoutMode = mode;
        try {
            drawGraph(groups, parent, tableTab, tableView);
        } catch (ServerDisconnect e) {
            e.printStackTrace();
        }
    }

    private void initZoomAndDrag() {
        setOnScroll((ScrollEvent e) -> {
            double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
            scale *= delta;
            graphContainer.setScaleX(scale);
            graphContainer.setScaleY(scale);
        });
        final double[] start = new double[2];
        setOnMousePressed(e -> {
            start[0] = e.getX();
            start[1] = e.getY();
        });
        setOnMouseDragged(e -> {
            graphContainer.setTranslateX(graphContainer.getTranslateX() + e.getX() - start[0]);
            graphContainer.setTranslateY(graphContainer.getTranslateY() + e.getY() - start[1]);
            start[0] = e.getX();
            start[1] = e.getY();
        });
    }

    private void drawGraph(List<StudyGroup> groups, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        graphContainer.getChildren().clear();
        nodes.clear();
        edges.clear();
        positions.clear();
        if (currentLayoutMode == LayoutMode.SCATTERED) {
            drawScattered(groups, parent, tableTab, tableView);
        } else {
            drawCircular(groups, parent, tableTab, tableView);
        }
    }

    private void drawScattered(List<StudyGroup> groups, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        int n = groups.size();
        double width = Math.max(getWidth(), 600);
        double height = Math.max(getHeight(), 400);
        double area = width * height * AREA_MULTIPLIER;
        double k = Math.sqrt(area / n);
        double centerX = width / 2;
        double centerY = height / 2;
        double initRadius = Math.min(width, height) / 2 * 1.2;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            positions.add(new double[]{
                    centerX + initRadius * Math.cos(angle),
                    centerY + initRadius * Math.sin(angle)
            });
        }
        double temperature = Math.max(width, height) / 10;
        for (int it = 0; it < ITERATIONS; it++) {
            double[][] disp = new double[n][2];
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double dx = positions.get(i)[0] - positions.get(j)[0];
                    double dy = positions.get(i)[1] - positions.get(j)[1];
                    double dist = Math.hypot(dx, dy) + 0.01;
                    double force = (k * k) / dist;
                    double ux = dx / dist;
                    double uy = dy / dist;
                    disp[i][0] += ux * force;
                    disp[i][1] += uy * force;
                    disp[j][0] -= ux * force;
                    disp[j][1] -= uy * force;
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    StudyGroup a = groups.get(i);
                    StudyGroup b = groups.get(j);
                    if (related(a, b)) {
                        double dx = positions.get(i)[0] - positions.get(j)[0];
                        double dy = positions.get(i)[1] - positions.get(j)[1];
                        double dist = Math.hypot(dx, dy) + 0.01;
                        double force = (dist * dist) / k;
                        double ux = dx / dist;
                        double uy = dy / dist;
                        disp[i][0] -= ux * force;
                        disp[i][1] -= uy * force;
                        disp[j][0] += ux * force;
                        disp[j][1] += uy * force;
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                double dx = disp[i][0];
                double dy = disp[i][1];
                double dist = Math.hypot(dx, dy);
                positions.get(i)[0] += (dx / dist) * Math.min(dist, temperature);
                positions.get(i)[1] += (dy / dist) * Math.min(dist, temperature);
            }
            temperature *= COOLING_FACTOR;
        }
        for (int i = 0; i < n; i++) {
            double x = positions.get(i)[0];
            double y = positions.get(i)[1];
            StudyGroup group = groups.get(i);
            boolean own = !Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID, group.getId())).contains("false");
            Color fill = own ? Color.LIGHTGREEN : Color.LIGHTGRAY;
            Color stroke = own ? Color.GREEN.darker() : Color.GRAY.darker();
            double r = Math.min(10 + Math.log10(group.getStudentCount()) * 10, 60);
            Circle node = new Circle(x, y, r);
            node.setFill(fill.deriveColor(1, 1, 1, 0.7));
            node.setStroke(stroke);
            node.setStrokeWidth(2);
            node.setPickOnBounds(true);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
            node.setOnMouseEntered(e -> { st.setToX(1.5); st.setToY(1.5); st.playFromStart(); });
            node.setOnMouseExited(e -> { st.setToX(1.0); st.setToY(1.0); st.playFromStart(); });
            int idx = i;
            node.setOnMouseClicked(e -> handleNodeClick(idx, parent, tableTab, tableView));
            Text label = new Text(group.getId().toString());
            label.setFont(Font.font(14));
            label.setFill(Color.BLACK);
            label.setX(x - r / 2);
            label.setY(y + r / 4);
            nodes.add(node);
            graphContainer.getChildren().addAll(node, label);
        }
        Platform.runLater(() -> drawEdges(tableTab, tableView));
    }

    private void drawCircular(List<StudyGroup> groups, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        int n = groups.size();
        double width = getWidth(), height = getHeight();
        double baseRadius = Math.min(width, height) / 2 - 50;
        if (baseRadius <= 0) baseRadius = 200;
        double extra = Math.max(0, n - 5) * 20;
        double radius = baseRadius + extra;
        double centerX = width / 2 <= 0 ? 300 : width / 2;
        double centerY = height / 2 <= 0 ? 300 : height / 2;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            StudyGroup group = groups.get(i);
            boolean own = !Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID, group.getId())).contains("false");
            Color fill = own ? Color.LIGHTGREEN : Color.LIGHTGRAY;
            Color stroke = own ? Color.GREEN.darker() : Color.GRAY.darker();
            double r = Math.min(10 + Math.log10(group.getStudentCount()) * 10, 60);
            Circle node = new Circle(x, y, r);
            node.setFill(fill.deriveColor(1, 1, 1, 0.7));
            node.setStroke(stroke);
            node.setStrokeWidth(2);
            node.setPickOnBounds(true);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
            node.setOnMouseEntered(e -> { st.setToX(1.5); st.setToY(1.5); st.playFromStart(); });
            node.setOnMouseExited(e -> { st.setToX(1.0); st.setToY(1.0); st.playFromStart(); });
            int idx = i;
            node.setOnMouseClicked(e -> handleNodeClick(idx, parent, tableTab, tableView));
            Text label = new Text(group.getId().toString());
            label.setFont(Font.font(14));
            label.setFill(Color.BLACK);
            label.setX(x - r / 2);
            label.setY(y + r / 4);
            nodes.add(node);
            graphContainer.getChildren().addAll(node, label);
        }
        Platform.runLater(() -> drawEdges(tableTab, tableView));
    }

    private void handleNodeClick(int idx, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) {
        Platform.runLater(() -> new UpdateDialog(parent, groups.get(idx).getId().toString()).show());
    }

    private boolean related(StudyGroup a, StudyGroup b) {
        return a.getGroupAdmin().equals(b.getGroupAdmin())
                || a.getFormOfEducation().equals(b.getFormOfEducation())
                || a.getSemester().equals(b.getSemester());
    }

    private void drawEdges(Tab tableTab, TableView<StudyGroup> tableView) {
        int n = groups.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                StudyGroup a = groups.get(i);
                StudyGroup b = groups.get(j);
                Color color;
                String lbl;
                if (a.getGroupAdmin().equals(b.getGroupAdmin())) {
                    lbl = a.getGroupAdmin().name(); color = Color.PURPLE;
                } else if (a.getFormOfEducation().equals(b.getFormOfEducation())) {
                    lbl = a.getFormOfEducation().toString(); color = Color.DARKBLUE;
                } else if (a.getSemester().equals(b.getSemester())) {
                    lbl = a.getSemester().toString(); color = Color.DARKRED;
                } else continue;
                Circle ca = nodes.get(i);
                Circle cb = nodes.get(j);
                double x1 = ca.getCenterX(), y1 = ca.getCenterY();
                double x2 = cb.getCenterX(), y2 = cb.getCenterY();
                Line edge = new Line(x1, y1, x2, y2);
                edge.setStroke(color);
                edge.setStrokeWidth(1);
                edge.setOpacity(0.6);
                int idx = i;
                edge.setOnMouseClicked(e -> selectGroup(idx, tableTab, tableView));
                double mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
                double dx = x2 - x1, dy = y2 - y1, len = Math.hypot(dx, dy);
                double nx = -dy / len, ny = dx / len, off = 10;
                Text t = new Text(mx + nx * off, my + ny * off, lbl);
                t.setFont(Font.font(12));
                t.setFill(color.darker());
                edges.add(edge);
                graphContainer.getChildren().addAll(edge, t);
            }
        }
    }

    private void selectGroup(int idx, Tab tableTab, TableView<StudyGroup> tableView) {
        StudyGroup g = groups.get(idx);
        tableView.getSelectionModel().select(g);
        tableView.scrollTo(g);
        tableTab.getTabPane().getSelectionModel().select(tableTab);
    }

    public void refreshGraph(List<StudyGroup> newGroups, MainView parent, Tab tableTab, TableView<StudyGroup> tableView) {
        this.groups = newGroups;
        try {
            drawGraph(newGroups, parent, tableTab, tableView);
        } catch (ServerDisconnect e) {
            e.printStackTrace();
        }
    }
}