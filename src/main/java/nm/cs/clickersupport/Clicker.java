package nm.cs.clickersupport;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Clicker {
    private Timeline timeLine;
    private boolean isClicking;

    List<FloatingWidget> closedStages;

    public Clicker(){
        this.closedStages = new ArrayList<>();
        this.isClicking = false;
    }

    public boolean getClickingStatus(){ return this.isClicking; }
    public void swapClickingStatus() {
        this.isClicking = !isClicking;
    }

    public void startClicking(double interval, int clickCount){
        this.isClicking = true;

        timeLine = new Timeline();

        for (int i = 0; i < clickCount; i++) {
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i * interval), event -> performClick())
            );
        }

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    public void startGradualClicking(CirclesManager cManager, List<FloatingWidget> openedStages, double interval) {
        this.isClicking = true;

        timeLine = new Timeline();

        Map<Integer, Point> map = cManager.getCoords();

        Platform.runLater(() -> {
            for (FloatingWidget widget : openedStages) {
                widget.getStage().hide();
                closedStages.add(widget);
            }
        });

        int clickIndex = 0;
        for (int i = 1; i <= map.size(); i++) {
            Point point = map.get(i);
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(clickIndex * interval), event -> performClickAt(point))
            );
            clickIndex++;
        }

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    public void startOneTimeClicking(CirclesManager cManager){
        this.isClicking = true;
    }

    public void stopClicking(){
        this.isClicking = false;
        if(timeLine != null){
            timeLine.stop();
        }
    }

    public void stopClickingGradual(){
        this.isClicking = false;
        if(timeLine != null){
            timeLine.stop();
            reopenAllWidgets();
        }
    }

    private void performClick(){
        try{
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

            Robot robot = new Robot();

            robot.mouseMove(mouseLocation.x, mouseLocation.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            System.out.println("Clicked at: " + mouseLocation.x + ", " + mouseLocation.y);
        }
        catch(AWTException e){
            e.printStackTrace();
        }
    }

    private void performClickAt(Point point){
        try {
            Robot robot = new Robot();

            robot.mouseMove(point.x, point.y);

            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Clicked at: " + point.x + ", " + point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void reopenAllWidgets() {
        Platform.runLater(() -> {
            for (FloatingWidget widget : closedStages) {
                widget.getStage().show();
                System.out.println("Size " + closedStages.size());
            }
            closedStages.clear();
        });
    }
}
