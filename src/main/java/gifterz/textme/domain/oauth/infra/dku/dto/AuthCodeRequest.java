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
            checkCodeChallenge(codeChallenge);
            codeChallengeMethod = checkCodeChallengeMethodIsNull(codeChallengeMethod);
            return new AuthCodeRequest(authType, codeChallenge, codeChallengeMethod);
        }
        throw new RuntimeException("지원하지 않는 소셜 로그인입니다.");
    }

    private static String checkCodeChallengeMethodIsNull(String codeChallengeMethod) {
        if (codeChallengeMethod == null) {
            codeChallengeMethod = "S256";
        }
        return codeChallengeMethod;
    }

    private static void checkCodeChallenge(String codeChallenge) {
        if (codeChallenge == null) {
            throw new RuntimeException("codeChallenge가 필요합니다.");
        }
    }

    public PkceRequest toPkceRequest() {
        return PkceRequest.of(codeChallenge, codeChallengeMethod);
    }
}
