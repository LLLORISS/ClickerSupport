package nm.cs.clickersupport;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.InputEvent;

public class Clicker {
    private Timeline timeLine;
    private boolean isClicking;

    public Clicker(){
        this.isClicking = false;
    }

    public boolean getClickingStatus(){ return this.isClicking; }
    public void swapClickingStatus() { this.isClicking = !isClicking; }

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

    public void stopClicking(){
        this.isClicking = false;
        if(timeLine != null){
            timeLine.stop();
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
}
