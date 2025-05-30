package start;

import org.springframework.web.client.RestClientException;
import rest.client.CharityCaseClient;
import teledon.model.CharityCase;
import teledon.services.rest.ServiceException;

import java.util.List;

public class StartRestTemplateClient {
    private final static CharityCaseClient charityCaseClient =new CharityCaseClient();
    public static void main(String[] args) {
        //  RestTemplate restTemplate=new RestTemplate();
        CharityCase charityCase = new CharityCase("caz de test",500);
        try{

            System.out.println("Adding a new charity case "+charityCase);
            show(()-> System.out.println(charityCaseClient.create(charityCase)));
            System.out.println("\n  Printing all users ...");
            show(()->{
                List<CharityCase> charityCases = charityCaseClient.getAll();
                for(CharityCase c:charityCases){
                    System.out.println(c.getId()+": "+c.getName());
                }
            });
        }catch(RestClientException ex){
            System.out.println("Exception ... "+ex.getMessage());
        }

        System.out.println("\n  Info for user with id=ana");
        show(()-> System.out.println(charityCaseClient.getById("ana")));
    }



    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception"+ e);
        }
    }
}
