package teledon.services.notification;

import org.springframework.stereotype.Component;
import teledon.model.CharityCase;

import java.util.List;
@Component
public interface TeledonNotificationService {
    void charityCasesUpdated(CharityCase[] updatedCharityCases);
}
