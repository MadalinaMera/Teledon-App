package teledon.services;

import teledon.model.CharityCase;
import teledon.model.Donor;
import teledon.model.Volunteer;

import java.util.List;

public interface ITeledonServices {
    Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException;
    List<CharityCase> findAllCases() throws TeledonException;
    List<Donor> findAllDonors() throws TeledonException;
    void donate(String name, String number, String address, Integer sum, Integer idCharityCase) throws TeledonException;
    void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException;
}
