package nm.cs.clickersupport;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChangeClickerTypeController {

    @FXML
    private CheckBox checkBoxClicking;
    @FXML
    private CheckBox checkBoxGradual;
    @FXML
    private CheckBox checkBoxOneTime;

    @FXML
    private Button tooltipButtonClicking;
    @FXML
    private Button tooltipButtonGradual;
    @FXML
    private Button tooltipButtonOneTime;

    @FXML
    private Button confirmButton;

    private ClickerMainController mainController;

    @FXML
    public void initialize() {
        setupExclusiveCheckBox(checkBoxClicking, checkBoxGradual, checkBoxOneTime);
        setupExclusiveCheckBox(checkBoxGradual, checkBoxClicking, checkBoxOneTime);
        setupExclusiveCheckBox(checkBoxOneTime, checkBoxClicking, checkBoxGradual);

        Tooltip tooltipClicking = new Tooltip("Clicking");
        tooltipClicking.setShowDelay(Duration.millis(200));
        tooltipClicking.setHideDelay(Duration.millis(100));
        tooltipButtonClicking.setTooltip(tooltipClicking);

        Tooltip tooltipGradual = new Tooltip("Gradual");
        tooltipGradual.setShowDelay(Duration.millis(200));
        tooltipGradual.setHideDelay(Duration.millis(100));
        tooltipButtonGradual.setTooltip(tooltipGradual);

        Tooltip tooltipOneTime = new Tooltip("One time");
        tooltipOneTime.setShowDelay(Duration.millis(200));
        tooltipOneTime.setHideDelay(Duration.millis(100));
        tooltipButtonOneTime.setTooltip(tooltipOneTime);

        if (this.mainController.getClickerType() == CLICKER_TYPE.CLICKING) {
            checkBoxClicking.setSelected(true);
        } else if (this.mainController.getClickerType() == CLICKER_TYPE.GRADUAL) {
            checkBoxGradual.setSelected(true);
        } else if (this.mainController.getClickerType() == CLICKER_TYPE.ONETIME) {
            checkBoxOneTime.setSelected(true);
        }
    }

    public void setMainController(ClickerMainController mainController) {
        this.mainController = mainController;
    }

    private void setupExclusiveCheckBox(CheckBox primary, CheckBox... others) {
        primary.setOnAction(event -> {
            if (primary.isSelected()) {
                for (CheckBox other : others) {
                    other.setSelected(false);
                }
            }
        });
    }

    @FXML
    protected void confirmChangeButton(){
        if (checkBoxClicking.isSelected()) {
            mainController.changeClickerType(CLICKER_TYPE.CLICKING);
        } else if (checkBoxGradual.isSelected()) {
            mainController.changeClickerType(CLICKER_TYPE.GRADUAL);
        } else if (checkBoxOneTime.isSelected()) {
            mainController.changeClickerType(CLICKER_TYPE.ONETIME);
        } else {
            System.out.println("[Clicker Support] CheckBox error");
            mainController.changeClickerType(CLICKER_TYPE.CLICKING);
        }

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
