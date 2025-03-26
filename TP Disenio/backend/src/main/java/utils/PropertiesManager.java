package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class PropertiesManager {
    private Properties properties;
    private String propertiesFilePath;

    public PropertiesManager(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFilePath)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + propertiesFilePath);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getPropertyString(String key) {
        return properties.getProperty(key);
    }

    public Integer getPropertyInteger(String key) {
        String value = properties.getProperty(key);
        return value != null ? Integer.valueOf(value) : null;
    }

    public Float getPropertyFloat(String key) {
        String value = properties.getProperty(key);
        return value != null ? Float.valueOf(value) : null;
    }

    public Double getPropertyDouble(String key) {
        String value = properties.getProperty(key);
        return value != null ? Double.valueOf(value) : null;
    }

    public Long getPropertyLong(String key) {
        String value = properties.getProperty(key);
        return value != null ? Long.valueOf(value) : null;
    }

    public Boolean getPropertyBoolean(String key) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.valueOf(value) : null;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    private void saveProperties() {
        // Not suitable for classpath resource updates. This should be adjusted for file system paths.
    }

    public ArrayList<String> getKeys() {
        Set<Object> keySet = properties.keySet();
        ArrayList<String> keys = new ArrayList<>();
        for (Object key : keySet) {
            keys.add((String) key);
        }
        return keys;
    }


    public static void main(String[] args) {
        PropertiesManager properties = new PropertiesManager("properties/tarjeta.properties");

        System.out.println(properties);
    }
}
