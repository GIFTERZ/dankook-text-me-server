package gifterz.textme.domain.notification.controller;

import gifterz.textme.domain.notification.service.NotificationService;
import gifterz.textme.global.auth.role.UserAuth;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @UserAuth
    public ResponseEntity<SseEmitter> subscribe(JwtAuthentication auth,
                                                @RequestParam(required = false) final String lastEventId) {
        SseEmitter sseEmitter = notificationService.subscribe(auth.getUserId(), lastEventId);
        return ResponseEntity.ok(sseEmitter);
    }
}
