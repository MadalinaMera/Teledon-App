package teledon.networking.objectProtocol;

import teledon.model.CharityCase;


public class CharityCasesUpdatedResponse implements Response {
    private final Iterable<CharityCase> updatedCharityCases;

    public CharityCasesUpdatedResponse(Iterable<CharityCase> updatedCharityCases) {
        this.updatedCharityCases = updatedCharityCases;
    }

    public Iterable<CharityCase> getUpdatedCharityCases() {
        return updatedCharityCases;
    }
}