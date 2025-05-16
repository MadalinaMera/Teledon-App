package teledon.services;

import teledon.model.CharityCase;
import teledon.model.Donor;

import java.util.List;

public interface ITeledonObserver {
    //void donorsUpdated(Iterable<Donor> updatedDonors) throws TeledonException;
    void donationsUpdated(List<CharityCase> updatedCharityCases) throws TeledonException;
}
