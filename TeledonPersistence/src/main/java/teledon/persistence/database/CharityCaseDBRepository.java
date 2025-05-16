package teledon.persistence.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.model.CharityCase;
import teledon.persistence.utils.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CharityCaseDBRepository implements ICharityCaseRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CharityCaseDBRepository(Properties props) {
        logger.info("Initializing CharityCaseDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public CharityCase findByName(String name) {
        logger.traceEntry("Finding charity case by name: {}", name);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM charity_cases WHERE name = ?")) {
            stmt.setString(1, name);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id_case");
                    String charityName = result.getString("name");
                    int sum = result.getInt("total_sum");
                    CharityCase charityCase = new CharityCase(charityName, sum);
                    charityCase.setId(id);
                    logger.traceExit(charityCase);
                    return charityCase;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public CharityCase findOne(Integer id) {
        logger.traceEntry("Finding charity case by id: {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM charity_cases WHERE id_case = ?")) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    String charityName = result.getString("name");
                    int sum = result.getInt("total_sum");
                    CharityCase charityCase = new CharityCase(charityName, sum);
                    charityCase.setId(id);
                    logger.traceExit(charityCase);
                    return charityCase;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public Iterable<CharityCase> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<CharityCase> charityCases = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM charity_cases")) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id_case");
                    String name = result.getString("name");
                    int sum = result.getInt("total_sum");
                    CharityCase charityCase = new CharityCase(name, sum);
                    charityCase.setId(id);
                    charityCases.add(charityCase);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit(charityCases);
        return charityCases;
    }

    @Override
    public CharityCase save(CharityCase entity) {
        logger.traceEntry("Saving charity case {}", entity);
        Connection con = dbUtils.getConnection();
        int result = 0;
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO charity_cases (name, total_sum) VALUES (?, ?)")) {
            stmt.setString(1, entity.getName());
            stmt.setInt(2, entity.getTotalSum());
            result = stmt.executeUpdate();
            logger.trace("Saved {} instances ",result);
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error saving element in  DB"+ e);
        }
        logger.traceExit();
        if(result == 0)
            return null;
        return entity;
    }

    @Override
    public CharityCase delete(Integer id) {
        throw new UnsupportedOperationException("Delete operation is not implemented");
    }

    @Override
    public CharityCase update(CharityCase entity) {
        logger.traceEntry("Updating charity case {}", entity);
        Connection con = dbUtils.getConnection();
        int result = 0;
        try (PreparedStatement stmt = con.prepareStatement("UPDATE charity_cases SET name = ?, total_sum = ? WHERE id_case = ?")) {
            stmt.setString(1, entity.getName());
            stmt.setInt(2, entity.getTotalSum());
            stmt.setInt(3, entity.getId());
            result = stmt.executeUpdate();
            logger.trace("Updated {} instances ", result);
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        if(result == 0)
            return null;
        return entity;
    }
}
