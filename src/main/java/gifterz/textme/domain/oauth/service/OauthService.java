package gifterz.textme.domain.oauth.service;

import gifterz.textme.domain.oauth.dto.OauthLoginRequest;
import gifterz.textme.domain.oauth.entity.OauthId;
import gifterz.textme.domain.oauth.entity.OauthMember;
import gifterz.textme.domain.oauth.infra.dku.dto.AuthCodeRequest;
import gifterz.textme.domain.oauth.util.AuthCodeRequestUrlProviderMapper;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.oauth.util.OauthMemberClientMapper;
import gifterz.textme.domain.security.entity.RefreshToken;
import gifterz.textme.domain.security.jwt.JwtUtils;
import gifterz.textme.domain.security.service.AesUtils;
import gifterz.textme.domain.security.service.RefreshTokenService;
import gifterz.textme.domain.user.dto.response.LoginResponse;
import gifterz.textme.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import gifterz.textme.domain.oauth.repository.OauthMemberRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {

    private final AuthCodeRequestUrlProviderMapper authCodeRequestUrlProviderMapper;
    private final OauthMemberRepository oauthMemberRepository;
    private final OauthMemberClientMapper oauthMemberClientMapper;
    private final JwtUtils jwtUtils;
    private final AesUtils aesUtils;
    private final RefreshTokenService refreshTokenService;

    public String getAuthCodeRequestUrl(AuthCodeRequest authCodeRequest) {
        return authCodeRequestUrlProviderMapper.provide(authCodeRequest.getAuthType(), authCodeRequest.toPkceRequest());
    }

    @Transactional
    public LoginResponse login(AuthType authType, OauthLoginRequest oauthLoginRequest) {
        String authCode = oauthLoginRequest.authCode();
        String codeVerifier = oauthLoginRequest.codeVerifier();
        if (codeVerifier != null) {
            return loginWithPkce(authType, authCode, codeVerifier);
        }
        OauthMember oauthMember = oauthMemberClientMapper.fetch(authType, authCode);
        return getLoginResponse(oauthMember);
    }

    private LoginResponse loginWithPkce(AuthType authType, String authCode, String codeVerifier) {
        OauthMember oauthMember = oauthMemberClientMapper.fetch(authType, authCode, codeVerifier);
        return getLoginResponse(oauthMember);
    }

    private LoginResponse getLoginResponse(OauthMember oauthMember) {
        OauthId oauthId = oauthMember.getOauthId();
        if (!oauthMemberRepository.existsByOauthId(oauthId)) {
            oauthMemberRepository.save(oauthMember);
        }
        User user = oauthMember.getUser();
        String email = user.getEmail();
        String accessToken = jwtUtils.generateAccessToken(email);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String encryptedUserId = encryptUserId(user.getId());
        return new LoginResponse(encryptedUserId, user.getEmail(), user.getName(), accessToken,
                refreshToken.getRefreshToken(), refreshToken.getCreatedAt());
    }

    private String encryptUserId(Long id) {
        String userId = id.toString();
        return aesUtils.encrypt(userId);
    }
}
