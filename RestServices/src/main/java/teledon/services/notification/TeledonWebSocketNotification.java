package teledon.services.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import teledon.model.CharityCase;
import teledon.services.websockets.TeledonWebsocketHandler;

@Component
public class TeledonWebSocketNotification implements TeledonNotificationService{

    @Autowired
    private TeledonWebsocketHandler webSocketHandler;

    public TeledonWebSocketNotification(){
        System.out.println("creating TeledonWebSocketNotification");
    }
    @Override
    public void charityCasesUpdated(CharityCase[] charityCases) {
        webSocketHandler.sendAllCharityCases(charityCases);
    }
}
