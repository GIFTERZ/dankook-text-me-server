package gifterz.textme.domain.oauth.infra.dku.dto;

import gifterz.textme.domain.oauth.entity.AuthType;
import lombok.Getter;


@Getter
public class AuthCodeRequest {
    private final AuthType authType;
    private final String codeChallenge;
    private final String codeChallengeMethod;

    private AuthCodeRequest(AuthType authType, String codeChallenge, String codeChallengeMethod) {
        this.authType = authType;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public static AuthCodeRequest of(AuthType authType, String codeChallenge, String codeChallengeMethod) {
        if (authType == AuthType.KAKAO) {
            return new AuthCodeRequest(authType, null, null);
        }
        if (authType == AuthType.DKU) {
            return new AuthCodeRequest(authType, codeChallenge, codeChallengeMethod);
        }
        throw new RuntimeException("지원하지 않는 소셜 로그인입니다.");
    }

    public PkceRequest toPkceRequest() {
        return PkceRequest.of(codeChallenge, codeChallengeMethod);
    }
}
