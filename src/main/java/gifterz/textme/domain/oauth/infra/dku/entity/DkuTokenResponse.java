package gifterz.textme.domain.oauth.infra.dku.entity;

public record DkuTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        String scope
) {
    public String createToken() {
        return this.tokenType + " " + this.accessToken;
    }
}
