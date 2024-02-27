package gifterz.textme.domain.oauth.infra.kakao.service;

import gifterz.textme.domain.oauth.entity.*;
import gifterz.textme.domain.oauth.infra.kakao.controller.KakaoApi;
import gifterz.textme.domain.oauth.infra.kakao.entity.*;
import gifterz.textme.domain.oauth.infra.kakao.config.KakaoOauthConfig;
import gifterz.textme.domain.oauth.infra.kakao.dto.KakaoMemberResponse;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RequiredArgsConstructor
public class KakaoMemberClient implements OauthMemberClient {
    private final KakaoApi kakaoApi;
    private final KakaoOauthConfig kakaoOauthConfig;
    private final UserRepository userRepository;

    @Override
    public AuthType supportServer() {
        return AuthType.KAKAO;
    }

    @Override
    public OauthMember fetchMember(String authCode, String... params) {
        KakaoToken kakaoToken = fetchKakaoToken(authCode);
        String bearerToken = "Bearer " + kakaoToken.accessToken();
        KakaoMemberResponse response = kakaoApi.fetchMemberInfo(bearerToken, APPLICATION_FORM_URLENCODED_VALUE);
        OauthId oauthId = OauthId.of(response.id(), AuthType.KAKAO);
        KakaoAccount kakaoAccount = response.kakaoAccount();
        KakaoProfile kakaoProfile = kakaoAccount.getProfile();
        String email = kakaoAccount.getEmail();
        String nickname = kakaoProfile.getNickname();
        User user = getUser(email, nickname);
        if (user.getAuthType() == AuthType.PASSWORD) {
            user.updateAuthType(AuthType.KAKAO);
        }
        return OauthMember.of(user, oauthId);
    }

    private User getUser(String email, String nickname) {
        return userRepository.findByEmail(email)
                .orElseGet(
                        () -> {
                            User newUser = User.of(email, nickname, AuthType.KAKAO);
                            userRepository.save(newUser);
                            return newUser;
                        }
                );
    }

    private KakaoToken fetchKakaoToken(String authCode) {
        return kakaoApi.fetchToken(getParamForKakaoToken(authCode));
    }

    private MultiValueMap<String, String> getParamForKakaoToken(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", kakaoOauthConfig.clientId());
        params.add("redirect_uri", kakaoOauthConfig.redirectUri());
        params.add("client_secret", kakaoOauthConfig.clientSecret());
        params.add("code", authCode);
        return params;
    }
}
