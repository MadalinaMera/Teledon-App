package start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = {
		"start",                        // pachetul aplicatiei
		"teledon.services.rest",       // controllerul REST
		"teledon.services.websockets", // websocket handler
		"teledon.services.notification", // notificari
		"teledon.persistence"
})
@SpringBootApplication
public class RestServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestServicesApplication.class, args);
	}

}
