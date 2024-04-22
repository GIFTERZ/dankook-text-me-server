package gifterz.textme.domain.oauth.entity;

import org.springframework.transaction.annotation.Transactional;

public interface OauthMemberClient {
    String grantType = "authorization_code";

    AuthType supportServer();

    @Transactional
    OauthMember fetchMember(String code, String... params);
}
