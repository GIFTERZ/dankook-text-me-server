package gifterz.textme.domain.notification.service;

import gifterz.textme.domain.notification.entity.Notification;
import gifterz.textme.domain.notification.repository.NotificationRepository;
import gifterz.textme.domain.notification.repository.SseEmitterRepository;
import gifterz.textme.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static io.lettuce.core.RedisURI.DEFAULT_TIMEOUT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = userId + "_" + System.currentTimeMillis();

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(emitterId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteById(emitterId));
        sseEmitter.onTimeout(() -> sseEmitterRepository.deleteById(emitterId));
        sseEmitter.onError((e) -> sseEmitterRepository.deleteById(emitterId));

        send(sseEmitter, emitterId, "Connected");

        if (lastEventId.isEmpty()) {
            Map<String, Object> events = sseEmitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> send(sseEmitter, entry.getKey(), entry.getValue()));
        }

        return sseEmitter;
    }

    @Transactional
    public Long sendNotification(User user, String message) {
        Notification notification = Notification.of(user, message);

        sseEmitterRepository.findAllEventStartWithId(String.valueOf(user.getId()))
                .forEach((key, emitter) -> {
                            sseEmitterRepository.saveEventCache(key, notification);
                            send(emitter, key, notification);
                        }
                );
        notificationRepository.save(notification);
        return notification.getId();
    }

    private void send(SseEmitter sseEmitter, String emitterId, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            sseEmitterRepository.deleteById(emitterId);
            sseEmitter.completeWithError(exception);
        }
    }

}
