package rest.client;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import teledon.model.CharityCase;
import teledon.services.rest.ServiceException;

import java.util.List;
import java.util.concurrent.Callable;


public class CharityCaseClient {
    public static final String URL = "http://localhost:8080/teledon/charity-cases";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public List<CharityCase> getAll() {
        return execute(() -> List.of(restTemplate.getForObject(URL, CharityCase[].class)));
    }

    public CharityCase getById(String id) {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), CharityCase.class));
    }

    public CharityCase create(CharityCase charityCase) {
        return execute(() -> restTemplate.postForObject(URL, charityCase, CharityCase.class));
    }

    public void update(CharityCase charityCase) {
        execute(() -> {
            restTemplate.put(String.format("%s/%s", URL, charityCase.getId()), charityCase);
            return null;
        });
    }

    public void delete(String id) {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }

}
