package teledon.networking.objectProtocol;

import teledon.model.Donor;


public class DonorsUpdatedRequest implements Request {
    private final Iterable<Donor> updatedDonors;

    public DonorsUpdatedRequest(Iterable<Donor> updatedDonors) {
        this.updatedDonors = updatedDonors;
    }

    public Iterable<Donor> getUpdatedDonors() {
        return updatedDonors;
    }
}