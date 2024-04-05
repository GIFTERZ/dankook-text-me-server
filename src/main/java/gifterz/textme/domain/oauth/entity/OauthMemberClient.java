package gifterz.textme.domain.oauth.entity;

public interface OauthMemberClient {
    String grantType = "authorization_code";

    AuthType supportServer();

    OauthMember fetchMember(String code, String... params);
}
