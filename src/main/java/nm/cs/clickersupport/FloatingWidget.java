package nm.cs.clickersupport;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FloatingWidget {
    private double offsetX;
    private double offsetY;
    private double stageX;
    private double stageY;

    private Circle widget;
    private Stage stage;

    public void start() {
        this.stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setOpacity(0);

        widget = new Circle(30, Color.RED);
        double stageWidth = 150;
        double stageHeight = 150;

        widget.setTranslateX((stageWidth - 2 * widget.getRadius()) / 2);
        widget.setTranslateY((stageHeight - 2 * widget.getRadius()) / 2);

        Pane pane = new Pane();
        pane.getChildren().add(widget);
        pane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setOpacity(1.0);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.show();

        widget.setOnMousePressed(this::onMousePressed);
        widget.setOnMouseDragged(this::onMouseDragged);
    }

    private void onMousePressed(MouseEvent event) {
        offsetX = event.getScreenX() - widget.getTranslateX();
        offsetY = event.getScreenY() - widget.getTranslateY();

        stageX = event.getScreenX() - stage.getX();
        stageY = event.getScreenY() - stage.getY();
    }

    private void onMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - stageX);
        stage.setY(event.getScreenY() - stageY);
    }
}
