package LMS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "config/database.properties";

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ignored) {
            // Defaults below keep the app runnable even before local config is created.
        }

        String url = valueOrDefault("LMS_DB_URL", properties.getProperty("db.url"), "jdbc:mysql://localhost:3306/lms");
        String user = valueOrDefault("LMS_DB_USER", properties.getProperty("db.user"), "root");
        String password = valueOrDefault("LMS_DB_PASSWORD", properties.getProperty("db.password"), "");

        return new DatabaseConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static String valueOrDefault(String envName, String configuredValue, String defaultValue) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        if (configuredValue != null && !configuredValue.trim().isEmpty()) {
            return configuredValue.trim();
        }

        return defaultValue;
    }
}
