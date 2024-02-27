package gifterz.textme.domain.oauth.infra.dku.service;

import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.oauth.entity.OauthId;
import gifterz.textme.domain.oauth.entity.OauthMember;
import gifterz.textme.domain.oauth.entity.OauthMemberClient;
import gifterz.textme.domain.oauth.infra.dku.config.DkuOauthConfig;
import gifterz.textme.domain.oauth.infra.dku.controller.DkuApi;
import gifterz.textme.domain.oauth.infra.dku.dto.DkuMemberResponse;
import gifterz.textme.domain.oauth.infra.dku.dto.MajorInfo;
import gifterz.textme.domain.oauth.infra.dku.entity.DkuTokenResponse;
import gifterz.textme.domain.user.entity.Major;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.repository.MajorRepository;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Component
@RequiredArgsConstructor
public class DkuMemberClient implements OauthMemberClient {
    private final DkuApi dkuApi;
    private final DkuOauthConfig dkuOauthConfig;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;

    @Override
    public AuthType supportServer() {
        return AuthType.DKU;
    }

    @Override
    public OauthMember fetchMember(String authCode, String... params) {
        DkuTokenResponse dkuTokenResponse = fetchDkuToken(authCode, params[0]);
        String scope = dkuTokenResponse.scope();

        DkuMemberResponse response = dkuApi.fetchMemberInfo(dkuTokenResponse.createToken(), scope);
        OauthId oauthId = OauthId.of(response.userId(), AuthType.DKU);
        String email = response.studentIdToEmail();
        String name = response.name();
        MajorInfo majorInfo = response.major();
        Major major = Major.of(majorInfo.department(), majorInfo.name());
        if (!majorRepository.existsByDepartmentAndName(major.getDepartment(), major.getName())) {
            majorRepository.save(major);
        }
        User user = getUser(email, name, major);
        if (user.getAuthType() == AuthType.PASSWORD) {
            user.updateAuthType(AuthType.DKU);
        }
        return OauthMember.of(user, oauthId);
    }

    private User getUser(String email, String name, Major major) {
        return userRepository.findByEmail(email)
                .orElseGet(
                        () -> {
                            User newUser = User.of(email, name, AuthType.DKU, major);
                            userRepository.save(newUser);
                            return newUser;
                        }
                );
    }

    private DkuTokenResponse fetchDkuToken(String authCode, String codeVerifier) {
        return dkuApi.fetchToken(getParamForDkuToken(authCode, codeVerifier));
    }

    private MultiValueMap<String, String> getParamForDkuToken(String authCode, String codeVerifier) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", dkuOauthConfig.clientId());
        params.add("redirect_uri", dkuOauthConfig.redirectUri());
        params.add("client_secret", dkuOauthConfig.clientSecret());
        params.add("code", authCode);
        params.add("code_verifier", codeVerifier);
        return params;
    }
}