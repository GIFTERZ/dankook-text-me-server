package gifterz.textme.domain.oauth.infra.dku.dto;

import lombok.Getter;

@Getter
public class PkceRequest {
    private final String codeChallenge;
    private final String codeChallengeMethod;

    private PkceRequest(String codeChallenge, String codeChallengeMethod) {
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public static PkceRequest of(String codeChallenge, String codeChallengeMethod) {
        return new PkceRequest(codeChallenge, codeChallengeMethod);
    }
}
