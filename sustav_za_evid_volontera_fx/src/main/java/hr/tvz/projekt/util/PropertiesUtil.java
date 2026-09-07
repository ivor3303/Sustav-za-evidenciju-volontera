package hr.tvz.projekt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private PropertiesUtil() {}

    public static Properties loadFromResources(String fileName) {
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new IOException(fileName + " not found in resources");
            Properties p = new Properties();
            p.load(is);
            return p;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + fileName, e);
        }
    }
}
