package common;

import jdk.jfr.Description;
import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(ConfigUtil.class);

    static {
        String fileName = "./src/main/resources/config.properties";
        try (FileInputStream input = new FileInputStream(fileName)) {
            logger.info("Bat dau doc file config....");
            properties.load(input);
            logger.info("Tai file config thanh cong.");
        } catch (IOException e) {
            logger.error("Khong the doc file " + fileName, e);
        }
    }

    @Description("Lay tham so they key")
    public static String get (String key){
        String result = properties.getProperty(key);
        if (result == null)
            logger.error("Khong tim thay " + key);
        return result;
    }

    @Description("Lay tham so, kem gia tri mac dinh")
    public  static String get (String key, String defaultValue){
        String result = properties.getProperty(key, defaultValue);
        if (result.equals(defaultValue))
            logger.warn("Khong tim thay " + key + ", dung gia tri mac dinh.");
        return result;
    }
}
