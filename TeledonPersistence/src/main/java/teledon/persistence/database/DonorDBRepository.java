package teledon.persistence.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.persistence.interfaces.IDonorRepository;
import teledon.model.Donor;
import teledon.persistence.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonorDBRepository implements IDonorRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonorDBRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Donor findByName(String name) {
        logger.traceEntry("Finding donor by name: {}", name);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM donors WHERE name = ?")) {
            stmt.setString(1, name);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id_donor");
                    String address = result.getString("address");
                    String phone = result.getString("phone");
                    Donor d = new Donor(name, address, phone);
                    d.setId(id);
                    logger.traceExit(d);
                    return d;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
    @Override
    public Donor findOne(Integer id) {
        logger.traceEntry("Finding donor by id: {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM donors WHERE id_donor = ?")) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    String address = result.getString("address");
                    String phone = result.getString("phone");
                    Donor donor = new Donor(name, address, phone);
                    donor.setId(id);
                    logger.traceExit(donor);
                    return donor;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
    @Override
    public Iterable<Donor> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM donors")) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id_donor");
                    String name = result.getString("name");
                    String address = result.getString("address");
                    String phone = result.getString("phone");
                    Donor donor = new Donor(name, address, phone);
                    donor.setId(id);
                    donors.add(donor);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit(donors);
        return donors;
    }
    @Override
    public Donor save(Donor entity) {
        logger.traceEntry("Saving donor {}", entity);
        Connection con = dbUtils.getConnection();
        int result = 0;
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO donors (name, address, phone) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getAddress());
            stmt.setString(3, entity.getPhone());
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
    public Donor delete(Integer id) {
        throw new UnsupportedOperationException("Delete operation is not implemented");
    }

    @Override
    public Donor update(Donor entity) {
        throw new UnsupportedOperationException("Update operation is not implemented");
    }
}