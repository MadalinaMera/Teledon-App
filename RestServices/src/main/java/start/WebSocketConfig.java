package start;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import teledon.services.websockets.TeledonWebsocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Bean
    public TeledonWebsocketHandler createTeledonWebsocketHandler(){

        return new TeledonWebsocketHandler();
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(createTeledonWebsocketHandler(), "/teledonws").setAllowedOrigins("*");
        System.out.println("Registered WebSocket Handler for teledonws");
    }
    @PostConstruct
    public void postConstruct() {
        System.out.println("WebSocketConfig instantiated");
    }
}
