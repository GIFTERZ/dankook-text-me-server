package gifterz.textme.domain.admin.controller;

import gifterz.textme.domain.letter.dto.response.AdminEventLetterResponse;
import gifterz.textme.domain.letter.service.EventLetterService;
import gifterz.textme.global.auth.role.AdminAuth;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final EventLetterService eventLetterService;

    @AdminAuth
    @GetMapping("/letters/events/all")
    public ResponseEntity<List<AdminEventLetterResponse>> findAllLettersByStatus(
            JwtAuthentication auth,
            @RequestParam(required = false) String status
    ) {
        List<AdminEventLetterResponse> letterResponses = eventLetterService.findAllLettersByStatus(status);
        return ResponseEntity.ok().body(letterResponses);
    }

    @AdminAuth
    @PatchMapping("/letters/events/{letterId}/{status}")
    public ResponseEntity<Void> changeLetterStatus(JwtAuthentication auth,
                                                   @PathVariable("letterId") final Long letterId,
                                                   @PathVariable("status") String status) {
        eventLetterService.changeLetterStatus(letterId, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
