package teledon.networking.objectProtocol;

import teledon.model.CharityCase;


public class CharityCasesUpdatedRequest implements Request {
    private final Iterable<CharityCase> updatedCharityCases;

    public CharityCasesUpdatedRequest(Iterable<CharityCase> updatedCharityCases) {
        this.updatedCharityCases = updatedCharityCases;
    }

    public Iterable<CharityCase> getUpdatedCharityCases() {
        return updatedCharityCases;
    }
}