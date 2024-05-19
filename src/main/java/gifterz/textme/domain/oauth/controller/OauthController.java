package gifterz.textme.domain.oauth.controller;

import gifterz.textme.domain.oauth.dto.OauthLoginRequest;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.oauth.infra.dku.dto.AuthCodeRequest;
import gifterz.textme.domain.oauth.service.OauthService;
import gifterz.textme.domain.user.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.view.RedirectView;


@RequiredArgsConstructor
@RequestMapping("/oauth")
@RestController
public class OauthController {

    private final OauthService oauthService;

    @GetMapping("/{authType}")
    public RedirectView redirectAuthCodeRequestUrl(
            @PathVariable AuthType authType,
            @RequestParam(required = false) String codeChallenge,
            @RequestParam(required = false) String codeChallengeMethod) {
        AuthCodeRequest authCodeRequest = AuthCodeRequest.of(authType, codeChallenge, codeChallengeMethod);
        String redirectUrl = oauthService.getAuthCodeRequestUrl(authCodeRequest);
        return new RedirectView(redirectUrl);
    }

    @PostMapping("/login/{authType}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable AuthType authType,
            @RequestBody OauthLoginRequest oauthLoginRequest) {
        LoginResponse loginResponse = oauthService.login(authType, oauthLoginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }
}
