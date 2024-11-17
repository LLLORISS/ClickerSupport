package nm.cs.clickersupport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ClickerMainWindow extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClickerMainWindow.class.getResource("clicker-main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 800);
        scene.getStylesheets().add(ClickerMainWindow.class.getResource("/styles.css").toExternalForm());

        ClickerMainController controller = fxmlLoader.getController();

        controller.initializeNativeHook();

        stage.setTitle("ClickerSupport");
        stage.getIcons().add(new Image(ClickerMainWindow.class.getResourceAsStream("/icons/favicon2.png")));
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            System.out.println("[ClickerSupport] Program has been closed.");
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
