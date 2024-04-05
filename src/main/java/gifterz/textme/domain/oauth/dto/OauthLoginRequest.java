package gifterz.textme.domain.oauth.dto;

import jakarta.annotation.Nullable;

public record OauthLoginRequest(
        String authCode,
        @Nullable
        String codeVerifier
) {

}
