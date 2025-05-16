package teledon.networking.objectProtocol;


import teledon.model.Volunteer;

public class LoginRequest implements Request {
    private Volunteer volunteer;

    public LoginRequest(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }
}
