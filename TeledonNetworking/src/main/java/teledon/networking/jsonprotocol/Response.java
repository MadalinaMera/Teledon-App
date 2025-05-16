package teledon.networking.jsonprotocol;

import teledon.model.CharityCase;
import teledon.model.Donor;
import teledon.model.Volunteer;

public class Response {
    private Volunteer volunteer;
    private ResponseType type;
    private CharityCase[] charityCases;
    private Donor[] donors;
    private String errorMessage;

    public Response(){
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public CharityCase[] getCharityCases() {
        return charityCases;
    }

    public void setCharityCases(CharityCase[] charityCases) {
        this.charityCases = charityCases;
    }

    public Donor[] getDonors() {
        return donors;
    }

    public void setDonors(Donor[] donors) {
        this.donors = donors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    @Override
    public String toString() {
        return "Response{" +
                "volunteer=" + volunteer +
                ", type=" + type +
                ", charityCases=" + charityCases +
                ", donors=" + donors +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
