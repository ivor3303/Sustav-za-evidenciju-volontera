package hr.tvz.projekt.repository.jdbc;

import hr.tvz.projekt.entities.Organizer;
import hr.tvz.projekt.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrganizerJdbcRepository {

    public List<Organizer> findAll() {
        String sql = "SELECT name, city, email, age FROM organizers";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Organizer> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Organizer(
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("email"),
                        rs.getInt("age")
                ));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("findAll organizers failed", e);
        }
    }

    public void save(Organizer o) {
        String sql = "INSERT INTO organizers(name, city, email, age) VALUES (?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, o.getName());
            ps.setString(2, o.getCity());
            ps.setString(3, o.getEmail());
            ps.setInt(4, o.getAge());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("save organizer failed", e);
        }
    }
}
