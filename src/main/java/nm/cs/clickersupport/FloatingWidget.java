package nm.cs.clickersupport;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FloatingWidget {
    private double offsetX;
    private double offsetY;
    private double stageX;
    private double stageY;

    private ClickerCircle widget;
    private Stage stage;

    private CirclesManager cManager;

    public FloatingWidget(CirclesManager cManager){
        this.cManager = cManager;
    }

    public void start() {
        this.stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setOpacity(0);

        widget = new ClickerCircle("1", Color.RED,30);
        double stageWidth = 150;
        double stageHeight = 150;
        cManager.addCircle(widget);
        cManager.updateCirclesNumbers();

        widget.getCircle().setCenterX(stageWidth / 2);
        widget.getCircle().setCenterY(stageHeight / 2);

        Pane pane = new Pane();
        widget.addToPane(pane);
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

        widget.getCircle().setOnMousePressed(this::onMousePressed);
        widget.getCircle().setOnMouseDragged(this::onMouseDragged);
        widget.getText().setOnMousePressed(this::onMousePressed);
        widget.getText().setOnMouseDragged(this::onMouseDragged);

    }

    private void onMousePressed(MouseEvent event) {
        offsetX = event.getScreenX() - widget.getCircle().getTranslateX();
        offsetY = event.getScreenY() - widget.getCircle().getTranslateY();

        stageX = event.getScreenX() - stage.getX();
        stageY = event.getScreenY() - stage.getY();
    }

    private void onMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - stageX);
        stage.setY(event.getScreenY() - stageY);
    }

    public Stage getStage(){
        return this.stage;
    }
}
