package teledon.persistence.database;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.persistence.interfaces.IDonationRepository;
import teledon.persistence.interfaces.IDonorRepository;
import teledon.model.CharityCase;
import teledon.model.Donation;
import teledon.model.Donor;
import teledon.persistence.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class DonationDBRepository implements IDonationRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private final IDonorRepository donorRepo;
    private final ICharityCaseRepository charityCaseRepo;

    public DonationDBRepository(Properties props, IDonorRepository donorRepo, ICharityCaseRepository charityCaseRepo) {
        logger.info("Initializing DonorDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
        this.donorRepo = donorRepo;
        this.charityCaseRepo = charityCaseRepo;
    }
    @Override
    public Donation findOne(Integer id) {
        logger.traceEntry("Finding donation by id: {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM donations WHERE id_donation = ?")) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int donorId = result.getInt("id_donor");
                    int charityCaseId = result.getInt("id_case");
                    int sum = result.getInt("sum");
                    Donor donor = donorRepo.findOne(donorId);
                    CharityCase charityCase = charityCaseRepo.findOne(charityCaseId);
                    Donation donation = new Donation(donor, charityCase, sum);
                    donation.setId(id);
                    logger.traceExit(donation);
                    return donation;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
    @Override
    public Iterable<Donation> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Donation> donations = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM donations")) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id_donation");
                    int donorId = result.getInt("id_donor");
                    int charityCaseId = result.getInt("id_case");
                    int sum = result.getInt("sum");
                    Donor donor = donorRepo.findOne(donorId);
                    CharityCase charityCase = charityCaseRepo.findOne(charityCaseId);
                    charityCase.setId(charityCaseId);
                    Donation donation = new Donation(donor, charityCase, sum);
                    donation.setId(id);
                    donations.add(donation);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit(donations);
        return donations;
    }
    @Override
    public Donation save(Donation entity) {
        logger.traceEntry("Saving donation {}", entity);
        Connection con = dbUtils.getConnection();
        int result = 0;
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO donations (id_donor, id_case, sum) VALUES (?, ?, ?)")) {
            stmt.setInt(1, entity.getDonor().getId());
            stmt.setInt(2, entity.getCharityCase().getId());
            stmt.setInt(3, entity.getSum());
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
    public Donation delete(Integer id) {
        throw new UnsupportedOperationException("Delete operation is not implemented");
    }

    @Override
    public Donation update(Donation entity) {
        throw new UnsupportedOperationException("Update operation is not implemented");
    }
}
