package gifterz.textme.domain.oauth.infra.kakao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.kakao")
public record KakaoOauthConfig(
        String clientId,
        String redirectUri,
        String clientSecret,
        String[] scope
) {
}
