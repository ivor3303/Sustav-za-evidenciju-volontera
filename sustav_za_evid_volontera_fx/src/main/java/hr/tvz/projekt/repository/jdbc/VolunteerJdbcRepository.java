package hr.tvz.projekt.repository.jdbc;

import hr.tvz.projekt.entities.SkillType;
import hr.tvz.projekt.entities.Volunteer;
import hr.tvz.projekt.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VolunteerJdbcRepository {

    public List<Volunteer> findAll() {
        String sql = "SELECT name, city, email, age, skill_type FROM volunteers";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Volunteer> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Volunteer(
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        SkillType.valueOf(rs.getString("skill_type"))
                ));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("findAll volunteers failed", e);
        }
    }

    public void save(Volunteer v) {
        String sql = "INSERT INTO volunteers(name, city, email, age, skill_type) VALUES (?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getName());
            ps.setString(2, v.getCity());
            ps.setString(3, v.getEmail());
            ps.setInt(4, v.getAge());
            ps.setString(5, v.getSkillType().name());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("save volunteer failed", e);
        }
    }

    public Optional<Volunteer> findLastInserted() {
        String sql = """
        SELECT name, city, email, age, skill_type
        FROM volunteers
        ORDER BY id DESC
        FETCH FIRST 1 ROWS ONLY
    """;

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return Optional.empty();

            return Optional.of(new Volunteer(
                    rs.getString("name"),
                    rs.getString("city"),
                    rs.getString("email"),
                    rs.getInt("age"),
                    SkillType.valueOf(rs.getString("skill_type"))
            ));
        } catch (Exception e) {
            throw new RuntimeException("findLastInserted volunteers failed", e);
        }
    }

}
