package nm.cs.clickersupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static final String CONFIG_FILE = "config_clicker.json";

    public static void saveConfig(Config config) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(CONFIG_FILE), config);
            System.out.println("[ConfigManager]: Конфігурацію успішно збережено.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ConfigManager]: Помилка при збереженні конфігурації.");
        }
    }

    public static Config loadConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(CONFIG_FILE), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ConfigManager]: Помилка при завантаженні конфігурації. Завантажено значення за замовчуванням.");
            return new Config(0.25, 5);
        }
    }

    public static int getClickCount() {
        Config config = loadConfig();
        return config.getClickCount();
    }

    public static double getInterval() {
        Config config = loadConfig();
        return config.getInterval();
    }


}
