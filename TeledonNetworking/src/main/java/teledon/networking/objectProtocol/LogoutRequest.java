package teledon.networking.objectProtocol;
import teledon.model.Volunteer;


public class LogoutRequest implements Request {

    private Volunteer volunteer;

    public LogoutRequest(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }
}
