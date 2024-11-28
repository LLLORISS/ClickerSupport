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
import javafx.stage.Window;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

enum CLICKER_TYPE{
    CLICKING,
    GRADUAL,
    ONETIME
}

public class ClickerMainController {
    @FXML
    private Button toggleButton;
    @FXML
    private Button addCircle;
    @FXML
    private Button removeCircle;
    @FXML
    private Button tooltipButton;

    @FXML
    private Menu menuButton;

    private final Clicker clicker = new Clicker();
    private CLICKER_TYPE clickerType = CLICKER_TYPE.CLICKING;
    private CirclesManager cManager;
    private List<FloatingWidget> openedStages;

    private boolean altPressed = false;
    private boolean bPressed = false;

    private int clickCount;
    private double interval;

    @FXML
    private Label currentIntervalLabel;
    @FXML
    private Label currentClickCountLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label runTimeLabel;

    @FXML
    private Circle statusIndicator;

    private Timeline clickerTimer;
    private int elapsedTime = 0;

    private void importStyles(Scene scene){
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/buttons.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/labels.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/main.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/tooltips.css").toExternalForm());
    }

    public CLICKER_TYPE getClickerType(){
        return this.clickerType;
    }

    public void initialize() {
        Tooltip tooltip = new Tooltip("Hot keys for launching: { ALT + B }.");
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));

        tooltipButton.setTooltip(tooltip);

        this.cManager = new CirclesManager();
        this.openedStages = new ArrayList<>();

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

            Platform.runLater(()->{
                statusIndicator.setStyle("-fx-fill: green;");
                statusLabel.setText("Clicker started");

                toggleButton.setText("Stop Clicking");
                toggleButton.setStyle("-fx-background-color: red;");
            });

            toggleButton.setDisable(true);
            addCircle.setDisable(true);
            removeCircle.setDisable(true);
            menuButton.setDisable(true);
        } else {
            clicker.swapClickingStatus();

            Platform.runLater(()->{
                statusIndicator.setStyle("-fx-fill: red;");
                statusLabel.setText("Clicker stopped");

                toggleButton.setText("Start Clicking");
                toggleButton.setStyle("-fx-background-color: #27ae60;");
            });

            toggleButton.setDisable(false);
            addCircle.setDisable(false);
            removeCircle.setDisable(false);
            menuButton.setDisable(false);
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
        switch(this.clickerType){
            case CLICKER_TYPE.CLICKING -> {
                System.out.println("[ClickerSupport] Clicker mode: Clicking");

                loadConfigParameters();

                if (clicker.getClickingStatus()) {
                    this.swapClickingStatus(false);

                    this.stopTimer();
                    clicker.stopClicking();
                    System.out.println("[ClickerSupport] Stop button has been pressed");
                }
                else {
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
                    System.out.println("[ClickerSupport] Start button has been pressed");
                }
            }
            case CLICKER_TYPE.GRADUAL -> {
                if(cManager.getCount() == 0){
                    showGradualAlert();
                }
                else {
                    System.out.println("[ClickerSupport] Clicker mode: Gradual");

                    loadConfigParameters();

                    if (clicker.getClickingStatus()) {
                        this.swapClickingStatus(false);

                        this.stopTimer();
                        clicker.stopClickingGradual();

                        System.out.println("[ClickerSupport] Stop button has been pressed");
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
                        clicker.startGradualClicking(this.cManager, openedStages,this.interval);
                        System.out.println("[ClickerSupport] Start button has been pressed");
                    }
                }
            }
            case CLICKER_TYPE.ONETIME -> {
                if(cManager.getCount() == 0){
                    showGradualAlert();
                }
                else {
                    System.out.println("[ClickerSupport] Clicker mode: OneTime");

                    loadConfigParameters();

                    if (clicker.getClickingStatus()) {
                        this.swapClickingStatus(false);

                        this.stopTimer();
                        clicker.stopClickingOneTime();

                        System.out.println("[ClickerSupport] Stop button has been pressed");
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
                        clicker.startOneTimeClicking(this.cManager, openedStages,this.interval);
                        System.out.println("[ClickerSupport] Start button has been pressed");
                    }
                }
            }
            default -> {
                System.out.println("[ClickerSupport] Clicker type not found");
            }
        }
    }

    @FXML
    protected void openChangeClickerType(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-clicker-type-view.fxml"));
            loader.setControllerFactory(c -> {
                ChangeClickerTypeController controller = new ChangeClickerTypeController();
                controller.setMainController(this);
                return controller;
            });

            Scene scene = new Scene(loader.load(), 500, 400);
            this.importStyles(scene);

            Stage stage = new Stage();
            stage.setTitle("Change Clicker type");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
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

            Scene scene = new Scene(loader.load(), 400,400);
            this.importStyles(scene);

            Stage stage = new Stage();
            stage.setTitle("Change Parameters");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
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

    @FXML
    protected void addCircleButton(){
        if(cManager.getCount() < 5) {
            FloatingWidget widget = new FloatingWidget(this.cManager);
            openedStages.add(widget);
            widget.start();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Point limit reached");
            alert.setContentText("You can't add more than 5 click points");
            alert.showAndWait();
        }
    }

    @FXML
    protected void removeCircleButton(){
        if(cManager.getCount() != 0) {
            Stage lastOpenedStage = (Stage) Stage.getWindows()
                    .stream()
                    .filter(Window::isShowing)
                    .reduce((first, second) -> second)
                    .orElse(null);

            if (lastOpenedStage != null && cManager.getCount() != 0) {
                openedStages.removeLast();
                cManager.removeCircle();
                lastOpenedStage.close();

            }
        }
    }

    protected void changeClickerType(CLICKER_TYPE clickerType){
        this.clickerType = clickerType;
    }

    private void showGradualAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Click point not found");
        alert.setContentText("Add at least one click point");
        alert.showAndWait();
    }
}
