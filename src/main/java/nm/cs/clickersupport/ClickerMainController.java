package nm.cs.clickersupport;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClickerMainController {
    @FXML
    private Button toggleButton;
    @FXML
    private Button ChangeParameters;

    private final Clicker clicker = new Clicker();

    private boolean altPressed = false;
    private boolean bPressed = false;

    private int clickCount;
    private double interval;

    @FXML
    private Button tooltipButton;

    @FXML
    private Label currentIntervalLabel;
    @FXML
    private Label currentClickCountLabel;


    public void initialize() {
        Tooltip tooltip = new Tooltip("Гарячі клавіші для запуску: { ALT + B }.");
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));

        tooltipButton.setTooltip(tooltip);

        loadConfigParameters();
    }

    public void updateParametersLabel(Double newInterval, Integer newClickCount){
        this.currentIntervalLabel.setText(newInterval.toString());
        this.currentClickCountLabel.setText(newClickCount.toString());
    }

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

    private void loadConfigParameters() {
        ConfigManager cfgManager = new ConfigManager();
        this.clickCount = cfgManager.getClickCount();
        this.interval = cfgManager.getInterval();

        updateParametersLabel(this.interval, this.clickCount);
    }

    @FXML
    protected void toggleClick() {
        toggleButton.setDisable(true);

        loadConfigParameters();

        if (clicker.getClickingStatus()) {
            clicker.swapClickingStatus();

            clicker.stopClicking();
            Platform.runLater(() -> {
                toggleButton.setText("Start Clicking");
                toggleButton.setStyle("-fx-background-color: #27ae60;");
                toggleButton.setDisable(false);
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

            clicker.startClicking(this.interval,this.clickCount);
            Platform.runLater(() -> {
                toggleButton.setText("Stop Clicking");
                toggleButton.setStyle("-fx-background-color: red;");
            });
            System.out.println("[ClickerSupport]: Start button has been pressed");
        }
    }

    @FXML
    protected void openChangeParameters(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-parameters-view.fxml"));
            loader.setControllerFactory(c -> {
                ChangeParametersController controller = new ChangeParametersController();
                controller.setMainController(this);
                return controller;
            });

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Change Parameters");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
