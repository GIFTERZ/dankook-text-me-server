package gifterz.textme.domain.letter.controller;

import gifterz.textme.domain.letter.dto.request.EventLetterRequest;
import gifterz.textme.domain.letter.dto.response.AllEventLetterResponse;
import gifterz.textme.domain.letter.dto.response.EventLetterResponse;
import gifterz.textme.domain.letter.service.EventLetterService;
import gifterz.textme.global.auth.role.DkuAuth;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/letters/events")
@RequiredArgsConstructor
public class EventLetterController {
    private final EventLetterService eventLetterService;

    @PostMapping
    @DkuAuth
    public ResponseEntity<Void> sendLetter(JwtAuthentication auth, @RequestBody @Valid EventLetterRequest request) {
        eventLetterService.sendLetter(auth.getUserId(), request.toSenderInfo(), request.toTarget());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @DkuAuth
    public ResponseEntity<List<AllEventLetterResponse>> getLettersByGender(
            JwtAuthentication auth,
            @RequestParam(required = false) String gender
    ) {
        List<AllEventLetterResponse> letterResponses = eventLetterService.getLettersByGender(gender);
        return ResponseEntity.ok().body(letterResponses);
    }

    @GetMapping("/{id}")
    @DkuAuth
    public ResponseEntity<EventLetterResponse> findLetter(JwtAuthentication auth,
                                                          @PathVariable("id") final Long letterId) {
        EventLetterResponse response = eventLetterService.findLetter(auth.getUserId(), letterId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{letterId}/reports")
    @DkuAuth
    public ResponseEntity<Void> reportLetter(JwtAuthentication auth, @PathVariable("letterId") final Long letterId) {
        eventLetterService.reportLetter(letterId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user")
    @DkuAuth
    public ResponseEntity<List<EventLetterResponse>> findLettersByUser(JwtAuthentication auth) {
        List<EventLetterResponse> responses = eventLetterService.findLettersByUser(auth.getUserId());
        return ResponseEntity.ok().body(responses);
    }
}
