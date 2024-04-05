package gifterz.textme.domain.oauth.infra.dku.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.dku")
public record DkuOauthConfig(
        String clientId,
        String redirectUri,
        String clientSecret,
        String[] scope
) {
}
