package hr.tvz.projekt.util;

import java.sql.Connection;
import java.sql.Statement;

public final class BackupUtil {
    private BackupUtil() {}

    public static int backupTable(String tableName) {
        String safe = switch (tableName) {
            case "volunteers", "events", "organizers" -> tableName;
            default -> throw new IllegalArgumentException("Nepodržana tablica: " + tableName);
        };

        String backupName = safe + "_BACKUP";

        try (Connection c = DbUtil.getConnection();
             Statement s = c.createStatement()) {

            s.executeUpdate("DROP TABLE IF EXISTS " + backupName);
            s.executeUpdate("CREATE TABLE " + backupName + " AS SELECT * FROM " + safe);

            var rs = s.executeQuery("SELECT COUNT(*) FROM " + backupName);
            rs.next();
            return rs.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException("Backup failed for " + safe, e);
        }
    }
}
