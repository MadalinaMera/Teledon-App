package teledon.persistence.interfaces;

import teledon.model.CharityCase;

public interface ICharityCaseRepository extends Repository<Integer, CharityCase> {
    CharityCase findByName(String name);
}
