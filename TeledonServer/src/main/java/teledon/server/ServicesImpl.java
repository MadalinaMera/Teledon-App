package teledon.server;

import teledon.model.Donation;
import teledon.model.Volunteer;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.model.Donor;
import teledon.model.CharityCase;
import teledon.persistence.interfaces.IDonationRepository;
import teledon.persistence.interfaces.IDonorRepository;
import teledon.persistence.interfaces.IVolunteerRepository;
import teledon.services.ITeledonObserver;
import teledon.services.ITeledonServices;
import teledon.services.TeledonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.BCryptHashing.verifyPassword;

public class ServicesImpl implements ITeledonServices {
    private final IVolunteerRepository volunteerRepository;
    private final ICharityCaseRepository charityCaseRepository;
    private final IDonationRepository donationRepository;
    private final IDonorRepository donorRepository;
    private Map<String, ITeledonObserver> loggedVolunteers;
    private static final Logger logger = LogManager.getLogger(ServicesImpl.class);

    private final int defaultThreadsNo = 5;

    public ServicesImpl(IVolunteerRepository volunteerRepository, ICharityCaseRepository charityCaseRepository, IDonationRepository donationRepository, IDonorRepository donorRepository) {
        this.volunteerRepository = volunteerRepository;
        this.charityCaseRepository = charityCaseRepository;
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        loggedVolunteers = new ConcurrentHashMap<>();
    }

    public synchronized Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        teledon.model.Volunteer volunteer = volunteerRepository.findByUsername(username);
        if (volunteer != null) {
            if(verifyPassword(password, volunteer.getPassword())) {
                if (loggedVolunteers.get(username) != null)
                    throw new TeledonException("User already logged in!");
                loggedVolunteers.put(username, client);
                return volunteer;
            } else
                throw new TeledonException("Authentication failed");
        }
        else throw new TeledonException("Authentication failed");
    }
    public List<CharityCase> findAllCases() throws TeledonException{
        return (List<CharityCase>)charityCaseRepository.findAll();

    }
    public List<Donor> findAllDonors() throws TeledonException{
        return (List<Donor>)donorRepository.findAll();
    }
    private void addDonation(Integer idDonor, Integer idCharityCase, Integer sum){
        Donor d = donorRepository.findOne(idDonor);
        CharityCase c = charityCaseRepository.findOne(idCharityCase);
        Donation donation = new Donation(d, c, sum);
        donationRepository.save(donation);
        c.setTotalSum(c.getTotalSum() + sum);
        charityCaseRepository.update(c);
    }
    public synchronized void donate(String name, String number, String address, Integer sum, Integer idCharityCase) throws TeledonException {
        /// synchronized because we want to make sure that the donation is added correctly
        /// 2 volunteers cannot add a donation to the same charity case at the same time, it might result a wrong sum
        Donor donor = donorRepository.findByName(name);
        boolean newDonor = false;
        if (donor == null){
            donor = new teledon.model.Donor(name, address, number);
            donorRepository.save(donor);
            newDonor = true;
        }
        Integer idDonor = donorRepository.findByName(name).getId();
        addDonation(idDonor, idCharityCase, sum);

        // Notify all volunteers about the new donation
        notifyDonationMade(newDonor);
    }

    private void notifyDonationMade(boolean newDonor) {
        Iterable<CharityCase> updatedCases = charityCaseRepository.findAll();
//        Iterable<Donor> updatedDonors = newDonor ? donorRepository.findAll() : null;

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        for (ITeledonObserver observer : loggedVolunteers.values()) {
            executor.execute(() -> {
                try {
                    logger.debug("Notifying volunteer [{}] about new donation.", observer);
                    List<CharityCase> updatedCasesList = new ArrayList<>();
                    for (CharityCase charityCase : updatedCases) {
                        updatedCasesList.add(charityCase);
                    }
                    CharityCase[] updatedCasesArray = new CharityCase[updatedCasesList.size()];
                    updatedCasesArray = updatedCasesList.toArray(updatedCasesArray);
                    observer.donationsUpdated(Arrays.stream(updatedCasesArray).toList());
//                    if (newDonor && updatedDonors != null) {
//                        observer.donorsUpdated(updatedDonors);
//                    }
                } catch (TeledonException e) {
                    logger.error("Error notifying volunteer: {}", String.valueOf(e));
                }
            });
        }

        executor.shutdown();
    }

    public synchronized void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        String username = volunteer.getUsername();
        //logger.traceEntry("Logging out volunteer {}", username);
        ITeledonObserver localClient = loggedVolunteers.remove(username);
        if (localClient == null)
            throw new TeledonException("Volunteer " + username + " is not logged in.");
    }

}