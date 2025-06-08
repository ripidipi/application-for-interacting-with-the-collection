package gui;

import collection.StudyGroup;
import commands.Commands;
import exceptions.ServerDisconnect;
import io.Authentication;
import io.Server;
import javafx.application.Platform;
import javafx.scene.Group;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphView extends Pane {
    private final Group graphContainer = new Group();
    private double scale = 1.0;
    private final List<StudyGroup> groups;
    private final List<Circle> nodes = new ArrayList<>();
    private final List<Line> edges = new ArrayList<>();
    private final Map<String, Color> adminColors = new HashMap<>();

    public GraphView(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        this.groups = groups;
        getChildren().add(graphContainer);
        initZoomAndDrag();
        drawGraph(groups, tableTab, tableView);
    }

    private void initZoomAndDrag() {
        setOnScroll(e -> {
            double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
            scale *= delta;
            graphContainer.setScaleX(scale);
            graphContainer.setScaleY(scale);
        });
        final double[] start = new double[2];
        setOnMousePressed(e -> {
            start[0] = e.getX(); start[1] = e.getY();
        });
        setOnMouseDragged(e -> {
            graphContainer.setTranslateX(graphContainer.getTranslateX() + e.getX() - start[0]);
            graphContainer.setTranslateY(graphContainer.getTranslateY() + e.getY() - start[1]);
            start[0] = e.getX(); start[1] = e.getY();
        });
    }

    private void drawGraph(List<StudyGroup> groups, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        graphContainer.getChildren().clear(); nodes.clear(); edges.clear();
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
            node.setFill(fill.deriveColor(1,1,1,0.7)); node.setStroke(stroke);
            node.setStrokeWidth(2); node.setPickOnBounds(true);
            int idx = i;
            node.setOnMouseClicked(e -> selectGroup(idx, tableTab, tableView));
            Text label = new Text(group.getId().toString());
            label.setFont(Font.font(14)); label.setFill(Color.BLACK);
            label.setX(x - r/2); label.setY(y + r/4);
            nodes.add(node); graphContainer.getChildren().addAll(node, label);
        }
        Platform.runLater(() -> drawEdges(tableTab, tableView));
    }

    private void drawEdges(Tab tableTab, TableView<StudyGroup> tableView) {
        int n = groups.size();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                StudyGroup a = groups.get(i), b = groups.get(j);
                Color color; String lbl;
                if (a.getGroupAdmin().equals(b.getGroupAdmin())) { lbl = a.getGroupAdmin().name(); color = Color.PURPLE; }
                else if (a.getFormOfEducation().equals(b.getFormOfEducation())) { lbl = a.getFormOfEducation().toString(); color = Color.DARKBLUE; }
                else if (a.getSemester().equals(b.getSemester())) { lbl = a.getSemester().toString(); color = Color.DARKRED; }
                else continue;
                Circle ca = nodes.get(i), cb = nodes.get(j);
                double x1=ca.getCenterX(), y1=ca.getCenterY(), x2=cb.getCenterX(), y2=cb.getCenterY();
                Line edge = new Line(x1, y1, x2, y2);
                edge.setStroke(color); edge.setStrokeWidth(1); edge.setOpacity(0.6);
                int idx = i; edge.setOnMouseClicked(e -> selectGroup(idx, tableTab, tableView));
                double mx=(x1+x2)/2, my=(y1+y2)/2;
                double dx=x2-x1, dy=y2-y1, len=Math.hypot(dx,dy);
                double nx=-dy/len, ny=dx/len, off=10;
                Text t=new Text(mx+nx*off, my+ny*off, lbl);
                t.setFont(Font.font(12)); t.setFill(color.darker());
                edges.add(edge); graphContainer.getChildren().addAll(edge, t);
            }
        }
    }

    private void selectGroup(int idx, Tab tableTab, TableView<StudyGroup> tableView) {
        StudyGroup g = groups.get(idx);
        tableView.getSelectionModel().select(g);
        tableView.scrollTo(g);
        tableTab.getTabPane().getSelectionModel().select(tableTab);
    }

    public void refreshGraph(List<StudyGroup> newGroups, Tab tableTab, TableView<StudyGroup> tableView) throws ServerDisconnect {
        drawGraph(newGroups, tableTab, tableView);
    }
}
