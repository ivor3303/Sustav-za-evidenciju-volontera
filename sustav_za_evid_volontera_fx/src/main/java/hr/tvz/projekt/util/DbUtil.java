package hr.tvz.projekt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DbUtil {
    private static final Properties DB = PropertiesUtil.loadFromResources("database.properties");

    private DbUtil() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DB.getProperty("url"),
                    DB.getProperty("username"),
                    DB.getProperty("password")
            );
        } catch (SQLException e) {
            throw new RuntimeException("DB connection failed", e);
        }
    }
}
