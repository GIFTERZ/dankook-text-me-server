package gifterz.textme.domain.letter.controller;

import gifterz.textme.domain.letter.dto.request.EventLetterRequest;
import gifterz.textme.domain.letter.service.EventLetterService;
import gifterz.textme.global.auth.role.UserAuth;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/letters/events")
@RequiredArgsConstructor
public class EventLetterController {
    private final EventLetterService eventLetterService;

    @PostMapping
    @UserAuth
    public ResponseEntity<Void> sendLetter(JwtAuthentication auth, @RequestBody @Valid EventLetterRequest request) {
        eventLetterService.sendLetter(auth.getUserId(), request.toSenderInfo(), request.toTarget());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}