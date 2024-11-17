package nm.cs.clickersupport;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClickerMainController {
    @FXML
    private Button toggleButton;
    private final Clicker clicker = new Clicker();

    private boolean altPressed = false;
    private boolean bPressed = false;

    public void initializeNativeHook(){
        try{
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyAdapter(){
                @Override
                public void nativeKeyPressed(NativeKeyEvent event){
                    handleNativeKeyPress(event);
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent event) {
                    handleNativeKeyRelease(event);
                }
            });
        }
        catch(NativeHookException e){
            e.printStackTrace();
        }
    }

    private void handleNativeKeyPress(NativeKeyEvent event){
        if (event.getKeyCode() == NativeKeyEvent.VC_ALT) {
            this.altPressed = true;
        }
        if (event.getKeyCode() == NativeKeyEvent.VC_B) {
            this.bPressed = true;
        }
        if (this.altPressed && this.bPressed) {
            toggleClick();
        }
    }

    private void handleNativeKeyRelease(NativeKeyEvent event) {
        if (event.getKeyCode() == NativeKeyEvent.VC_ALT) {
            this.altPressed = false;
        }
        if (event.getKeyCode() == NativeKeyEvent.VC_B) {
            this.bPressed = false;
        }
    }

    @FXML
    protected void toggleClick() {
        toggleButton.setDisable(true);

        if (clicker.getClickingStatus()) {
            clicker.swapClickingStatus();

            clicker.stopClicking();
            Platform.runLater(() -> {
                toggleButton.setText("Start Clicking");
            });
            System.out.println("[ClickerSupport]: Stop button has been pressed");
        } else {
            clicker.swapClickingStatus();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> {
                        Platform.runLater(() -> {
                            toggleButton.setDisable(false);
                        });
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();

            clicker.startClicking(0.25,5);
            Platform.runLater(() -> {
                toggleButton.setText("Stop Clicking");
            });
            System.out.println("[ClickerSupport]: Start button has been pressed");
        }
    }
}
