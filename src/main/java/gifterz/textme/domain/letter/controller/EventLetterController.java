package gifterz.textme.domain.letter.controller;

import gifterz.textme.domain.letter.dto.request.EventLetterRequest;
import gifterz.textme.domain.letter.dto.response.AllEventLetterResponse;
import gifterz.textme.domain.letter.dto.response.EventLetterResponse;
import gifterz.textme.domain.letter.service.EventLetterService;
import gifterz.textme.global.auth.role.UserAuth;
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
    @UserAuth
    public ResponseEntity<Void> sendLetter(JwtAuthentication auth, @RequestBody @Valid EventLetterRequest request) {
        eventLetterService.sendLetter(auth.getUserId(), request.toSenderInfo(), request.toTarget());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @UserAuth
    public ResponseEntity<List<AllEventLetterResponse>> getLettersByGender(JwtAuthentication auth, @RequestParam String gender) {
        List<AllEventLetterResponse> letterResponses = eventLetterService.getLettersByGender(gender);
        return ResponseEntity.ok().body(letterResponses);
    }
  
    @GetMapping("/{id}")
    @UserAuth
    public ResponseEntity<EventLetterResponse> findLetter(JwtAuthentication auth, @PathVariable("id") final Long letterId) {
        EventLetterResponse response = eventLetterService.findLetter(auth.getUserId(), letterId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{letterId}/reports")
    @UserAuth
    public ResponseEntity<Void> reportLetter(JwtAuthentication auth, @PathVariable("letterId") final Long letterId) {
        eventLetterService.reportLetter(letterId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
