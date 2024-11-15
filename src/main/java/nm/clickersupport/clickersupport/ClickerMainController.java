package nm.clickersupport.clickersupport;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClickerMainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Clicker Application!");
    }
}