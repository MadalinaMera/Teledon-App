package start;

import org.springframework.web.client.RestClientException;
import rest.client.CharityCaseClient;
import teledon.model.CharityCase;
import teledon.services.rest.ServiceException;

import java.util.List;
import java.util.concurrent.Callable;

public class StartRestClient {

    private final static CharityCaseClient charityCaseClient = new CharityCaseClient();

    public static void main(String[] args) {

        try {
            System.out.println("============= CREATE =============");
            CharityCase newCase = new CharityCase("Caz Java", 1000);
            CharityCase created = showWithResult(() -> charityCaseClient.create(newCase));
            System.out.println("Creat: " + created);

            System.out.println("\n============= READ ONE =============");
            if (created != null) {
                CharityCase found = showWithResult(() -> charityCaseClient.getById(String.valueOf(created.getId())));
                System.out.println("Gasit: " + found);
            }

            System.out.println("\n============= UPDATE =============");
            if (created != null) {
                created.setName("Caz Java (actualizat)");
                show(() -> charityCaseClient.update(created));
                System.out.println("Actualizat: " + created);
            }

            System.out.println("\n============= READ ALL =============");
            show(() -> {
                List<CharityCase> cases = charityCaseClient.getAll();
                cases.forEach(c -> System.out.println(c.getId() + ": " + c.getName()));
            });

            System.out.println("\n============= DELETE =============");
            if (created != null) {
                show(() -> charityCaseClient.delete(String.valueOf(created.getId())));
                System.out.println("Sters cazul cu ID: " + created.getId());
            }

        } catch (RestClientException e) {
            System.out.println("Exceptie REST: " + e.getMessage());
        }
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            System.out.println("Service exception: " + e.getMessage());
        }
    }

    private static <T> T showWithResult(Callable<T> task) {
        try {
            return task.call();
        } catch (Exception e) {
            System.out.println("Service exception: " + e.getMessage());
            return null;
        }
    }
}