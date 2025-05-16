package teledon.persistence.interfaces;

import teledon.model.Donor;

public interface IDonorRepository extends Repository<Integer, Donor> {
    Donor findByName(String name);
}
