package nm.cs.clickersupport;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ChangeParametersController {
    @FXML
    private TextField clicksField;
    @FXML
    private TextField intervalField;

    private Config config;

    private ClickerMainController mainController;

    public void setMainController(ClickerMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    protected void saveParameters(){
        int clicksCount = Integer.parseInt(clicksField.getText());
        double interval = Double.parseDouble(intervalField.getText());

        config.setClickCount(clicksCount);
        config.setInterval(interval);

        ConfigManager.saveConfig(config);

        if (mainController != null) {
            mainController.updateParametersLabel(interval, clicksCount);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuration has been saved");
        alert.setHeaderText("Your settings have been saved");

        alert.showAndWait();
    }

    public void initialize(){
        config = ConfigManager.loadConfig();
        clicksField.setText(String.valueOf(config.getClickCount()));
        intervalField.setText(String.valueOf(config.getInterval()));
    }
}
