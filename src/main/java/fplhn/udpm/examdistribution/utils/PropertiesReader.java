package fplhn.udpm.examdistribution.utils;

import fplhn.udpm.examdistribution.infrastructure.constant.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesReader {

    private static final Properties properties = new Properties();

    static {
        try (
                InputStream inputStream = PropertiesReader
                        .class
                        .getClassLoader()
                        .getResourceAsStream(
                                Constants.FileProperties.PROPERTIES_CONFIGURATIONS
                        )
        ) {
            if (inputStream == null) {
                throw new IOException("File application.properties không tồn tại");
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            throw new ExceptionInInitializerError(e);
        }
    }

    public String getPropertyConfig(String key) {
        return properties.getProperty(key);
    }

}
