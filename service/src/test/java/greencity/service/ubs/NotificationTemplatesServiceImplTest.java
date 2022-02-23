package greencity.service.ubs;

import greencity.ModelUtils;
import greencity.dto.UpdateNotificationTemplatesDto;
import greencity.entity.enums.NotificationType;
import greencity.repository.NotificationTemplateRepository;
import greencity.service.NotificationTemplatesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationTemplatesServiceImplTest {
    @InjectMocks
    private NotificationTemplatesServiceImpl notificationTemplatesService;
    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @Test
    void updateNotificationTemplateForSITE() {
        UpdateNotificationTemplatesDto updateNotificationTemplatesDto = ModelUtils.getUpdateNotificationTemplatesDto();
        notificationTemplatesService.updateNotificationTemplateForSITE("test", NotificationType.UNPAID_ORDER.toString(),
            1);
        verify(notificationTemplateRepository).updateNotificationTemplateForSITE(
            updateNotificationTemplatesDto.getBody(),
            updateNotificationTemplatesDto.getNotificationType(),
            updateNotificationTemplatesDto.getLanguageId());
    }

    @Test
    void updateNotificationTemplateForOTHER() {
        UpdateNotificationTemplatesDto updateNotificationTemplatesDto = ModelUtils.getUpdateNotificationTemplatesDto();
        notificationTemplatesService.updateNotificationTemplateForOTHER("test",
            NotificationType.UNPAID_ORDER.toString(), 1);
        verify(notificationTemplateRepository).updateNotificationTemplateForOTHER(
            updateNotificationTemplatesDto.getBody(),
            updateNotificationTemplatesDto.getNotificationType(),
            updateNotificationTemplatesDto.getLanguageId());
    }
}