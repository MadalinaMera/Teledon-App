package teledon.persistence.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.persistence.interfaces.IVolunteerRepository;
import teledon.model.Volunteer;
import teledon.persistence.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class VolunteerDBRepository implements IVolunteerRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public VolunteerDBRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Volunteer findByUsername(String username) {
        logger.traceEntry("Finding volunteer by username: {}", username);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM volunteers WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id_volunteer");
                    String password = result.getString("password");
                    Volunteer v = new Volunteer(username, password);
                    v.setId(id);
                    logger.traceExit(v);
                    return v;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
    public Volunteer findOne(Integer id) {
        logger.traceEntry("Finding volunteer by id: {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM volunteers WHERE id_volunteer = ?")) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Volunteer volunteer = new Volunteer(username, password);
                    volunteer.setId(id);
                    logger.traceExit(volunteer);
                    return volunteer;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    public Iterable<Volunteer> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Volunteer> volunteers = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM volunteers")) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id_volunteer");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Volunteer volunteer = new Volunteer(username, password);
                    volunteer.setId(id);
                    volunteers.add(volunteer);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit(volunteers);
        return volunteers;
    }

    public Volunteer save(Volunteer entity) {
        logger.traceEntry("Saving volunteer {}", entity);
        Connection con = dbUtils.getConnection();
        int result = 0;
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO volunteers (username, password) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPassword());
            result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.trace("Saved {} instances ", result);
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return result > 0 ? entity : null;
    }
    @Override
    public Volunteer delete(Integer id) {
        throw new UnsupportedOperationException("Delete operation is not implemented");
    }

    @Override
    public Volunteer update(Volunteer entity) {
        throw new UnsupportedOperationException("Update operation is not implemented");
    }
}