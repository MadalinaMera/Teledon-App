package teledon.persistence.interfaces;


import teledon.model.Volunteer;

public interface IVolunteerRepository extends Repository<Integer, Volunteer> {
    Volunteer findByUsername(String username);
}
