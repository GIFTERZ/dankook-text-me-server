package gifterz.textme.domain.oauth.infra.dku.service;

import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.oauth.entity.OauthId;
import gifterz.textme.domain.oauth.entity.OauthMember;
import gifterz.textme.domain.oauth.entity.OauthMemberClient;
import gifterz.textme.domain.oauth.infra.dku.config.DkuOauthConfig;
import gifterz.textme.domain.oauth.infra.dku.controller.DkuApi;
import gifterz.textme.domain.oauth.infra.dku.dto.DkuMemberResponse;
import gifterz.textme.domain.oauth.infra.dku.entity.DkuTokenResponse;
import gifterz.textme.domain.oauth.infra.dku.vo.DkuStudentInfo;
import gifterz.textme.domain.user.entity.Major;
import gifterz.textme.domain.user.entity.Member;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.EmailDuplicatedException;
import gifterz.textme.domain.user.repository.MajorRepository;
import gifterz.textme.domain.user.repository.MemberRepository;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DkuMemberClient implements OauthMemberClient {
    private final DkuApi dkuApi;
    private final DkuOauthConfig dkuOauthConfig;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final MemberRepository memberRepository;

    @Override
    public AuthType supportServer() {
        return AuthType.DKU;
    }

    @Override
    public OauthMember fetchMember(String authCode, String... params) {
        DkuTokenResponse dkuTokenResponse = fetchDkuToken(authCode, params[0]);
        String scope = dkuTokenResponse.scope();
        DkuMemberResponse response = dkuApi.fetchMemberInfo(dkuTokenResponse.createToken(), scope);
        DkuStudentInfo dkuStudentInfo = response.toDkuStudentInfo();
        OauthId oauthId = OauthId.of(dkuStudentInfo.getUserId(), AuthType.DKU);
        String email = dkuStudentInfo.getEmail();
        String name = dkuStudentInfo.getNickname();
        String gender = dkuStudentInfo.getGender();

        Major major = getMajor(dkuStudentInfo);
        User user = getUser(email, name, major, gender);

        return OauthMember.of(user, oauthId);
    }


    private Major getMajor(DkuStudentInfo dkuStudentInfo) {
        Major major = dkuStudentInfo.toMajor();
        Optional<Major> majorOptional = majorRepository.findByDepartmentAndName(major.getDepartment(), major.getName());
        if (majorOptional.isPresent()) {
            return majorOptional.get();
        }
        majorRepository.save(major);
        return major;
    }

    private User getUser(String email, String name, Major major, String gender) {
        Optional<User> userExists = userRepository.findByEmail(email);
        if (userExists.isPresent()) {
            User originUser = userExists.get();
            checkEmailDuplicated(originUser, major, gender);
            return originUser;
        }
        User newUser = User.of(email, name, AuthType.DKU, major, gender);
        userRepository.save(newUser);
        return newUser;
    }

    private void checkEmailDuplicated(User user, Major major, String gender) {
        if (user.getAuthType() == AuthType.DKU) {
            return;
        }
        if (user.getAuthType() == AuthType.PASSWORD) {
            updateToOauthClient(user, major, gender);
            Member member = memberRepository.findByUser(user).orElseThrow();
            member.deactiveMember();
            return;
        }
        throw new EmailDuplicatedException(user.getEmail());
    }

    private static void updateToOauthClient(User user, Major major, String gender) {
        user.updateAuthType(AuthType.DKU);
        user.updateMajor(major);
        user.updateGender(gender);
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