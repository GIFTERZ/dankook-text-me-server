package gifterz.textme.domain.letter.controller;

import gifterz.textme.domain.letter.dto.request.PrizeLetterRequest;
import gifterz.textme.domain.letter.dto.response.AllPrizeLettersResponse;
import gifterz.textme.domain.letter.dto.response.PrizeLetterResponse;
import gifterz.textme.domain.letter.service.PrizeLetterService;
import gifterz.textme.global.auth.role.AdminAuth;
import gifterz.textme.global.auth.role.DkuAuth;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/letters/prizes")
@RequiredArgsConstructor
public class PrizeLetterController {
    private final PrizeLetterService prizeLetterService;

    @PostMapping
    @DkuAuth
    public ResponseEntity<Void> sendLetter(JwtAuthentication auth, @Valid PrizeLetterRequest request) {
        prizeLetterService.sendLetter(auth.getUserId(), request.toPrizeLetterVO());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @AdminAuth
    public ResponseEntity<List<AllPrizeLettersResponse>> getPrizeLetters(JwtAuthentication auth) {
        return ResponseEntity.ok(prizeLetterService.getPrizeLetters());
    }

    @GetMapping("{id}")
    @AdminAuth
    public ResponseEntity<PrizeLetterResponse> getPrizeLetter(JwtAuthentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(prizeLetterService.getPrizeLetter(id));
    }

}
