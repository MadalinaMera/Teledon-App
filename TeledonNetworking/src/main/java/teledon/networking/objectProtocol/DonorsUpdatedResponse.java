package teledon.networking.objectProtocol;

import teledon.model.Donor;


public class DonorsUpdatedResponse implements Response {
    private final Iterable<Donor> updatedDonors;

    public DonorsUpdatedResponse(Iterable<Donor> updatedDonors) {
        this.updatedDonors = updatedDonors;
    }

    public Iterable<Donor> getUpdatedDonors() {
        return updatedDonors;
    }
}