package hr.tvz.projekt.repository.jdbc;

import hr.tvz.projekt.entities.Event;
import hr.tvz.projekt.entities.EventType;
import hr.tvz.projekt.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EventJdbcRepository {

    public List<Event> findAll() {
        String sql = "SELECT title, city, address, type FROM events";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Event> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Event(
                        rs.getString("title"),
                        rs.getString("city"),
                        rs.getString("address"),
                        EventType.valueOf(rs.getString("type"))
                ));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("findAll events failed", e);
        }
    }

    public void save(Event e) {
        String sql = "INSERT INTO events(title, city, address, type) VALUES (?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getTitle());
            ps.setString(2, e.getCity());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getType().name());
            ps.executeUpdate();

        } catch (Exception ex) {
            throw new RuntimeException("save event failed", ex);
        }
    }
}
