package hr.tvz.projekt.util;

import java.sql.Connection;
import java.sql.Statement;

public final class SchemaUtil {
    private SchemaUtil() {}

    public static void init() {
        try (Connection c = DbUtil.getConnection(); Statement s = c.createStatement()) {

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS volunteers (
                  id IDENTITY PRIMARY KEY,
                  name VARCHAR(100) NOT NULL,
                  city VARCHAR(100) NOT NULL,
                  email VARCHAR(150) NOT NULL,
                  age INT NOT NULL,
                  skill_type VARCHAR(50) NOT NULL
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS events (
                  id IDENTITY PRIMARY KEY,
                  title VARCHAR(150) NOT NULL,
                  city VARCHAR(100) NOT NULL,
                  address VARCHAR(200) NOT NULL,
                  type VARCHAR(50) NOT NULL
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS organizers (
                  id IDENTITY PRIMARY KEY,
                  name VARCHAR(100) NOT NULL,
                  city VARCHAR(100) NOT NULL,
                  email VARCHAR(150) NOT NULL,
                  age INT NOT NULL
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Schema init failed", e);
        }
    }
}
