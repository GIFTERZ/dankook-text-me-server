package gifterz.textme.domain.letter.controller;

import gifterz.textme.domain.letter.dto.request.*;
import gifterz.textme.domain.letter.dto.response.AllLetterResponse;
import gifterz.textme.domain.letter.dto.response.LetterResponse;
import gifterz.textme.domain.letter.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/letters")
@RequiredArgsConstructor
public class LetterController {
    private final LetterService letterService;

    @PostMapping
    public ResponseEntity<LetterResponse> sendLetter(@RequestBody @Valid final LetterRequest request) {
        LetterResponse letterResponse = letterService.makeLetter(request);
        return ResponseEntity.ok().body(letterResponse);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<List<AllLetterResponse>> findLetters(@PathVariable("id") final String id) {
        List<AllLetterResponse> letterResponses = letterService.findLettersByUserId(id);
        return ResponseEntity.ok().body(letterResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LetterResponse> findLetter(@PathVariable("id") final Long id) {
        LetterResponse letterResponse = letterService.findLetter(id);
        return ResponseEntity.ok().body(letterResponse);
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendSlowLetterWithEmail(
            @RequestBody final SlowLetterWithEmailRequest request) {
        letterService.sendSlowLetterWithEmail(request.toSenderInfo(), request.toTarget());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/address")
    public ResponseEntity<Void> sendSlowLetterWithAddress(
            @RequestBody final SlowLetterWithAddressRequest request) {
        letterService.sendSlowLetterWithAddress(request.toSenderInfo(), request.toReceiverInfo(), request.contents());
        return ResponseEntity.ok().build();
    }
}
