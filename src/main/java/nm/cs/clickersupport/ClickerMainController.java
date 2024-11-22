package nm.cs.clickersupport;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClickerMainController {
    @FXML
    private Button toggleButton;

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

    @FXML
    private Circle statusIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private Label runTimeLabel;

    private Timeline clickerTimer;
    private int elapsedTime = 0;


    public void initialize() {
        Tooltip tooltip = new Tooltip("Гарячі клавіші для запуску: { ALT + B }.");
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));

        tooltipButton.setTooltip(tooltip);

        loadConfigParameters();

    }

    private void startTimer(){
        this.elapsedTime = 0;

        clickerTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTime++;
            Platform.runLater(() -> updateElapsedTime(elapsedTime));
        }));
        clickerTimer.setCycleCount(Timeline.INDEFINITE);
        clickerTimer.play();
    }

    private void stopTimer() {
        if (clickerTimer != null) {
            clickerTimer.stop();
        }
    }

    private void updateElapsedTime(int timeInSeconds) {
        this.runTimeLabel.setText("Час роботи: " + formatTime(timeInSeconds));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public void swapClickingStatus(boolean isActive){
        if (isActive) {
            clicker.swapClickingStatus();
            statusIndicator.setStyle("-fx-fill: green;");
            statusLabel.setText("Клікер увімкнено");
        } else {
            clicker.swapClickingStatus();
            statusIndicator.setStyle("-fx-fill: red;");
            statusLabel.setText("Клікер вимкнено");
        }
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
            this.swapClickingStatus(false);

            this.stopTimer();
            clicker.stopClicking();
            Platform.runLater(() -> {
                toggleButton.setText("Start Clicking");
                toggleButton.setStyle("-fx-background-color: #27ae60;");
                toggleButton.setDisable(false);
            });
            System.out.println("[ClickerSupport]: Stop button has been pressed");
        } else {
            this.swapClickingStatus(true);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> {
                        Platform.runLater(() -> {
                            toggleButton.setDisable(false);
                        });
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();

            this.startTimer();
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
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void exitAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Changes will not be saved.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }
}
