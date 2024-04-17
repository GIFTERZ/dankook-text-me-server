package gifterz.textme.domain.oauth.infra.dku.util;

import gifterz.textme.domain.oauth.infra.dku.config.DkuOauthConfig;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.oauth.util.AuthCodeRequestUrlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class DkuAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {
    private final DkuOauthConfig dkuOauthConfig;
    private static final String responseType = "code";
    @Value("${oauth.dku.api.authorize}")
    private String authorizeURI;

    @Override
    public AuthType supportServer() {
        return AuthType.DKU;
    }

    @Override
    public String provide(String... params) {
        return UriComponentsBuilder
                .fromUriString(authorizeURI)
                .queryParam("code_challenge", params[0])
                .queryParam("code_challenge_method", params[1])
                .queryParam("client_id", dkuOauthConfig.clientId())
                .queryParam("redirect_uri", dkuOauthConfig.redirectUri())
                .queryParam("response_type", responseType)
                .queryParam("scope", String.join(" ", dkuOauthConfig.scope()))
                .toUriString();
    }
}
