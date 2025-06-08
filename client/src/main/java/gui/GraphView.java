// ./src/main/java/gui/GraphView.java
package gui;

import collection.StudyGroup;
import commands.Commands;
import exceptions.ServerDisconnect;
import io.Server;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import storage.Request;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GraphView extends Pane {
    private final Group graphContainer = new Group();
    private double scale = 1.0;
    private final List<StudyGroup> groups;
    private final List<Node> nodes = new ArrayList<>();
    private final List<Line> edges = new ArrayList<>();
    private final Map<String, Color> adminColors = new HashMap<>();

    public GraphView(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        this.groups = groups;
        getChildren().add(graphContainer);
        initZoomAndDrag();
        drawGraph(groups, tableTab, tableView);
    }

    private void initZoomAndDrag() {
        setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale *= delta;
            graphContainer.setScaleX(scale);
            graphContainer.setScaleY(scale);
        });

        setOnMousePressed(event -> {
            final double[] startX = {event.getX()};
            final double[] startY = {event.getY()};
            setOnMouseDragged(dragEvent -> {
                graphContainer.setTranslateX(graphContainer.getTranslateX() + dragEvent.getX() - startX[0]);
                graphContainer.setTranslateY(graphContainer.getTranslateY() + dragEvent.getY() - startY[0]);
                startX[0] = dragEvent.getX();
                startY[0] = dragEvent.getY();
            });
        });
    }

    private void drawGraph(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        double angleStep = 360.0 / groups.size();
        double radius = 200;

        for (int i = 0; i < groups.size(); i++) {
            double angle = Math.toRadians(i * angleStep);
            double x = radius * Math.cos(angle) + getWidth() / 2;
            double y = radius * Math.sin(angle) + getHeight() / 2;

            StudyGroup group = groups.get(i);
            Color color = (!Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID,
                    group.getId())).contains("false")) ? Color.web("#4CAF50") : Color.web("#E0E0E0");

            int studentCount = group.getStudentCount();
            double circleRadius = Math.min(Math.log10(studentCount) * 10, 50) ;

            Circle node = new Circle(x, y, circleRadius);
            node.setFill(color.deriveColor(1, 1, 1, 0.5));
            node.setStroke(color);

            Text label = new Text(
                    x - circleRadius / 2,
                    y + circleRadius / 4,
                    "Group " + group.getId() + "\n" + group.getName()
            );
            label.setFill(Color.BLACK);

            int finalI = i;
            node.setOnMouseClicked(event -> {
                tableView.getSelectionModel().select(groups.get(finalI));
                tableView.scrollTo(groups.get(finalI));
                tableTab.getTabPane().getSelectionModel().select(tableTab);
            });

            nodes.add(node);
            graphContainer.getChildren().addAll(node, label);
        }
        Platform.runLater(() -> drawEdges(groups, tableTab, tableView));
    }

    private void drawEdges(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) {
        for (int i = 0; i < groups.size(); i++) {
            for (int j = i + 1; j < groups.size(); j++) {
                StudyGroup groupA = groups.get(i);
                StudyGroup groupB = groups.get(j);

                if (groupA.getGroupAdmin().equals(groupB.getGroupAdmin())) {
                    addEdge(i, j, groupB.getGroupAdmin().name(), Color.GRAY, tableTab, tableView);
                }
                if (groupA.getFormOfEducation().equals(groupB.getFormOfEducation())) {
                    addEdge(i, j, groupB.getFormOfEducation().toString(), Color.BLUE, tableTab, tableView);
                }
                if (groupA.getSemester().equals(groupB.getSemester())) {
                    addEdge(i, j, groupB.getSemester().toString(), Color.RED, tableTab, tableView);
                }
            }
        }
    }

    private void addEdge(int i, int j, String label, Color color, Tab tableTab, TableView<StudyGroup> tableView) {
        Circle circleA = (Circle) nodes.get(i);
        Circle circleB = (Circle) nodes.get(j);

        Line edge = new Line(
                circleA.getCenterX(), circleA.getCenterY(),
                circleB.getCenterX(), circleB.getCenterY()
        );
        edge.setStroke(color);
        edge.setStrokeWidth(2);

        Text edgeLabel = new Text(
                (circleA.getCenterX() + circleB.getCenterX()) / 2,
                (circleA.getCenterY() + circleB.getCenterY()) / 2,
                label
        );
        edgeLabel.setFill(color);
        edgeLabel.setFont(Font.font("Arial", 12));

        edge.setOnMouseClicked(event -> {
            tableView.getSelectionModel().select(groups.get(i));
            tableView.scrollTo(groups.get(i));
            tableTab.getTabPane().getSelectionModel().select(tableTab);
        });

        edges.add(edge);
        graphContainer.getChildren().addAll(edge, edgeLabel);
    }

    public void refreshGraph(List<StudyGroup> newGroups, Tab tableTab, TableView<StudyGroup> tableView)
            throws ServerDisconnect {
        graphContainer.getChildren().clear();
        nodes.clear();
        edges.clear();
        drawGraph(newGroups, tableTab, tableView);
        Platform.runLater(() -> this.drawEdges(newGroups, tableTab, tableView));
    }
}