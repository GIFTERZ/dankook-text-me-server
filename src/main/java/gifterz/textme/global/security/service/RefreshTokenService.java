package gifterz.textme.global.security.service;

import gifterz.textme.global.security.entity.RefreshToken;
import gifterz.textme.global.security.jwt.JwtUtils;
import gifterz.textme.global.security.repository.RefreshTokenRepository;
import gifterz.textme.domain.user.dto.response.TokenRefreshResponse;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.TokenRefreshException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${security.jwt.token.refreshExpirationMs}")
    private Long refreshTokenDurationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUser(user);
        String newRefreshToken = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.updateRefreshToken(newRefreshToken, expiryDate);
            return refreshToken;
        }
        RefreshToken refreshToken = new RefreshToken(user, newRefreshToken, expiryDate);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    public TokenRefreshResponse refreshTokens(String token) {
        RefreshToken refreshToken = findByRefreshToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "refreshToken이 DB에 존재하지 않습니다."));
        verifyExpiration(refreshToken);
        checkStatus(refreshToken);
        String newRefreshToken = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);
        refreshToken.updateRefreshToken(newRefreshToken, expiryDate);
        User user = refreshToken.getUser();
        String email = user.getEmail();
        String newToken = jwtUtils.generateAccessToken(user);
        return new TokenRefreshResponse(newToken, refreshToken.getRefreshToken(), refreshToken.getCreatedAt());
    }

    private Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getRefreshToken(),
                    "리프레시토큰이 만료되었습니다.");
        }
    }

    private void checkStatus(RefreshToken refreshToken) {
        if (!Objects.equals(refreshToken.getStatus(), "ACTIVATE")) {
            throw new TokenRefreshException(refreshToken.getRefreshToken(), "리프레시토큰이 만료되었습니다.");
        }
    }
}
