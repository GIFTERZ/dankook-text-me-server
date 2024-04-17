package gifterz.textme.domain.oauth.infra.kakao.util;

import gifterz.textme.domain.oauth.util.AuthCodeRequestUrlProvider;
import gifterz.textme.domain.oauth.infra.kakao.config.KakaoOauthConfig;
import gifterz.textme.domain.oauth.entity.AuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {
    private final KakaoOauthConfig kakaoOauthConfig;
    private static final String responseType = "code";
    @Value("${oauth.kakao.api.authorize}")
    private String authorizeURI;

    @Override
    public AuthType supportServer() {
        return AuthType.KAKAO;
    }

    @Override
    public String provide(String... params) {
        return UriComponentsBuilder
                .fromUriString(authorizeURI)
                .queryParam("client_id", kakaoOauthConfig.clientId())
                .queryParam("redirect_uri", kakaoOauthConfig.redirectUri())
                .queryParam("response_type", responseType)
                .queryParam("scope", String.join(" ", kakaoOauthConfig.scope()))
                .toUriString();
    }

}
