package greencity.service.notification;

import greencity.repository.NotificationTemplateRepository;
import greencity.service.ubs.NotificationTemplatesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationTemplatesServiceImpl implements NotificationTemplatesService {
    private final NotificationTemplateRepository notificationTemplateRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNotificationTemplateForSITE(String body, String notificationType, String languageCode) {
        notificationTemplateRepository.updateNotificationTemplateForSITE(body, notificationType, languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNotificationTemplateForOTHER(String body, String notificationType, String languageCode) {
        notificationTemplateRepository.updateNotificationTemplateForOTHER(body, notificationType, languageCode);
    }
}