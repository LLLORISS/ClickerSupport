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

    private Robot robot;

    public Clicker(){
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException("[ClickerSupport] Failed to create Robot instance");
        }

        this.closedStages = new ArrayList<>();
        this.isClicking = false;
    }

    public boolean getClickingStatus(){ return this.isClicking; }
    public void swapClickingStatus() {
        this.isClicking = !isClicking;
    }


    public void startClicking(CLICKER_TYPE type, double interval, int clickCount,
                              CirclesManager cManager, List<FloatingWidget> openedStages){
        this.isClicking = true;

        switch(type){
            case CLICKING -> {
                timeLine = new Timeline();

                for (int i = 0; i < clickCount; i++) {
                    timeLine.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(i * interval), event -> performClick())
                    );
                }

                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.play();
            }
            case GRADUAL -> {

                timeLine = new Timeline();

                Map<Integer, Point> map = cManager.getCoords();

                if (map.isEmpty()) {
                    System.out.println("[ClickerSupport] No points available for GRADUAL clicking.");
                    this.isClicking = false;
                    return;
                }


                Platform.runLater(() -> {
                    for (FloatingWidget widget : openedStages) {
                        widget.getStage().hide();
                        closedStages.add(widget);
                    }
                });

                if (map.size() == 1) {
                    Point point = map.values().iterator().next();
                    timeLine.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(interval), event -> performClick(point))
                    );
                    timeLine.setCycleCount(Timeline.INDEFINITE);
                    timeLine.play();
                } else {
                    int clickIndex = 0;
                    for (int i = 1; i <= map.size(); i++) {
                        Point point = map.get(i);
                        timeLine.getKeyFrames().add(
                                new KeyFrame(Duration.seconds(clickIndex * interval), event -> performClick(point))
                        );
                        clickIndex++;
                    }
                    timeLine.setCycleCount(Timeline.INDEFINITE);
                    timeLine.play();
                }
            }
            case ONETIME -> {
                Map<Integer, Point> map = cManager.getCoords();

                if (map.isEmpty()) {
                    System.out.println("[ClickerSupport] No points available for One Time clicking.");
                    this.isClicking = false;
                    return;
                }

                Platform.runLater(() -> {
                    for (FloatingWidget widget : openedStages) {
                        widget.getStage().hide();
                        closedStages.add(widget);
                    }
                });

                long clickDelay = 500;

                Thread clickThread = new Thread(() -> {
                    while (isClicking) {
                        if (!isClicking)
                        {
                            break;
                        }
                        for (Point point : map.values()) {
                            if (!isClicking) {
                                break;
                            }
                            performClick(point);

                            try {
                                Thread.sleep(clickDelay);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        try {
                            Thread.sleep((long) interval);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });

                clickThread.setDaemon(true);
                clickThread.start();
            }
            default -> {
                this.isClicking = false;
                System.out.println("[ClickerSupport] Error clicker type");
            }
        }
    }

    public void stopClicking(CLICKER_TYPE type){
        if(type == CLICKER_TYPE.CLICKING && timeLine != null){
            timeLine.stop();
            this.isClicking = false;
        }
        else if(type == CLICKER_TYPE.GRADUAL && timeLine != null){
            timeLine.stop();
            reopenAllWidgets();
            this.isClicking = false;
        }
        else if(type == CLICKER_TYPE.ONETIME){
            reopenAllWidgets();
            this.isClicking = false;
        }
    }

    private void performClick(){
        try{
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

            robot.mouseMove(mouseLocation.x, mouseLocation.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            System.out.println("Clicked at: " + mouseLocation.x + ", " + mouseLocation.y);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void performClick(Point point){
        try {
            robot.mouseMove(point.x, point.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Clicked at: " + point.x + ", " + point.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reopenAllWidgets() {
        Platform.runLater(() -> {
            if (!closedStages.isEmpty()) {
                for (FloatingWidget widget : closedStages) {
                    if (!widget.getStage().isShowing()) {
                        widget.getStage().show();
                    }
                }
                closedStages.clear();
            }
        });
    }
}
