package teledon.networking.jsonprotocol;

import teledon.model.Donation;
import teledon.model.Volunteer;
import teledon.networking.dto.DonationDTO;

public class Request {
    RequestType type;
    Volunteer volunteer;
    DonationDTO donation;

    public Request() {
    }
    public RequestType getType() {
        return type;
    }
    public void setType(RequestType type) {
        this.type = type;
    }
    public Volunteer getVolunteer() {
        return volunteer;
    }
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }
    public DonationDTO getDonation() {
        return donation;
    }

    public void setDonation(DonationDTO donation) {
        this.donation = donation;
    }
    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", volunteer=" + volunteer +
                ", donation=" + donation +
                '}';
    }
}
