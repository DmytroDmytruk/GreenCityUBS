package greencity.notificator;

import greencity.dto.notification.ScheduledNotificationDto;
import greencity.notificator.scheduler.NotificationTaskScheduler;
import greencity.repository.NotificationTemplateRepository;
import greencity.service.ubs.NotificationService;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static greencity.dto.notification.ScheduledNotificationDto.build;
import static greencity.enums.NotificationType.COURIER_ITINERARY_FORMED;

@Component
@RequiredArgsConstructor
public class CourierInternallyFormedNotificator implements ScheduledNotificator {
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationService notificationService;
    private final NotificationTaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Override
    public ScheduledNotificationDto notifyBySchedule() {
        closePreviousTaskIfPresent(scheduledFuture);
        var notificationSchedule = notificationTemplateRepository
            .findScheduleOfActiveTemplateByType(COURIER_ITINERARY_FORMED);

        return createNotificationScheduler(notificationSchedule);
    }

    private ScheduledNotificationDto createNotificationScheduler(String schedule) {
        scheduledFuture = taskScheduler.scheduleNotification(this::notifyCourierItineraryFormed, schedule,
            COURIER_ITINERARY_FORMED);
        return build(COURIER_ITINERARY_FORMED, this.getClass());
    }

    private void notifyCourierItineraryFormed() {
        notificationService.notifyAllCourierItineraryFormed();
    }
}
