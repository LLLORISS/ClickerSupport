package nm.cs.clickersupport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ClickerMainWindow extends Application {
    private static final String CONFIG_FILE = "config_clicker.json";

    private void importStyles(Scene scene){
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/buttons.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/config-box.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/labels.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/main.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/menu.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/status.css").toExternalForm());
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles/tooltips.css").toExternalForm());
    }

    @Override
    public void start(Stage stage) throws IOException {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                ConfigManager cfgManager = new ConfigManager();
                Config cfg = new Config(0.25,5);
                cfgManager.saveConfig(cfg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(ClickerMainWindow.class.getResource("clicker-main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 800);
        this.importStyles(scene);

        ClickerMainController controller = fxmlLoader.getController();

        controller.initializeNativeHook();

        stage.setTitle("ClickerSupport");
        stage.getIcons().add(new Image(ClickerMainWindow.class.getResourceAsStream("/icons/favicon2.png")));
        stage.setScene(scene);

        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            System.out.println("[ClickerSupport] Program has been closed.");
            System.exit(0);
        });

        stage.show();

        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        double windowWidth = stage.getWidth();
        double windowHeight = stage.getHeight();

        stage.setX((screenWidth - windowWidth) / 2);
        stage.setY((screenHeight - windowHeight) / 2);
    }

    public static void main(String[] args) {
        launch();
    }
}
