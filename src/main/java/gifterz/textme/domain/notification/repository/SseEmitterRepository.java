package gifterz.textme.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SseEmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(final String emitterId, final SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);

        return sseEmitter;
    }

    public void saveEventCache(String eventId, Object event) {
        eventCache.put(eventId, event);
    }

    public Optional<SseEmitter> get(final String emitterId) {
        SseEmitter sseEmitter = emitters.get(emitterId);

        return Optional.ofNullable(sseEmitter);
    }

    public Map<String, SseEmitter> findAllEventStartWithId(final String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().indexOf("_") == userId.length() && entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithId(final String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().indexOf("_") == userId.length() && entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(final String emitterId) {
        emitters.remove(emitterId);
    }

    public void deleteAllEmittersStartWithId(final String userId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.indexOf("_") == userId.length() && key.startsWith(userId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    public void deleteAllEventCacheStartWithId(final String userId) {
        eventCache.forEach(
                (key, data) -> {
                    if (key.indexOf("_") == userId.length() && key.startsWith(userId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
