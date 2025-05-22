package teledon;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.model.Volunteer;
import teledon.networking.utils.AbstractServer;
import teledon.networking.utils.ServerException;
import teledon.networking.utils.TeledonJsonConcurrentServer;
import teledon.persistence.database.CharityCaseDBRepository;
import teledon.persistence.database.DonationDBRepository;
import teledon.persistence.database.DonorDBRepository;
import teledon.persistence.database.VolunteerDBRepository;
import teledon.persistence.hibernate.CharityCaseHibernateRepository;
import teledon.persistence.hibernate.VolunteerHibernateRepository;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.persistence.interfaces.IDonationRepository;
import teledon.persistence.interfaces.IDonorRepository;
import teledon.persistence.interfaces.IVolunteerRepository;
import teledon.persistence.utils.HibernateUtils;
import teledon.server.ServicesImpl;
import teledon.services.ITeledonServices;

public class StartJsonServer {
    private static int defaultPort=55555;
    private static Logger logger = LogManager.getLogger(StartJsonServer.class);
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartJsonServer.class.getResourceAsStream("/teledonserver.properties"));
            logger.info("Server properties set. {} ", serverProps);
            //serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find teledonserver.properties "+e);
            logger.debug("Looking for file in "+(new File(".")).getAbsolutePath());
            return;
        }
        //IVolunteerRepository userRepo=new VolunteerDBRepository(serverProps);
        //ICharityCaseRepository charityCaseRepo=new CharityCaseDBRepository(serverProps);
        IVolunteerRepository userRepo = new VolunteerHibernateRepository();
        logger.info("Volunteer repository: Hibernate");
        ICharityCaseRepository charityCaseRepo = new CharityCaseHibernateRepository();
        logger.info("CharityCase repository: Hibernate");
        IDonorRepository donorRepository=new DonorDBRepository(serverProps);
        logger.info("Donor repository: JDBC");
        IDonationRepository donationRepository=new DonationDBRepository(serverProps, donorRepository, charityCaseRepo);
        logger.info("Donation repository: JDBC");
        ITeledonServices services=new ServicesImpl(userRepo,charityCaseRepo,donationRepository,donorRepository);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("teledon.server.port"));
        }catch (NumberFormatException nef){
            logger.error("Wrong  Port Number"+nef.getMessage());
            logger.debug("Using default port "+defaultPort);
        }
        logger.debug("Starting server on port: "+chatServerPort);
        AbstractServer server = new TeledonJsonConcurrentServer(chatServerPort, services);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server" + e.getMessage());
        } finally {
            logger.info("Shutting down server ...");
            HibernateUtils.closeSessionFactory();
            logger.info("Server shut down. Hibernate session factory closed.");
        }

    }
}