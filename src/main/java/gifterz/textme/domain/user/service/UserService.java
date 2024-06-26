package gifterz.textme.domain.user.service;

import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.domain.user.exception.InvalidPasswordException;
import gifterz.textme.global.config.WebSecurityConfig;
import gifterz.textme.global.security.entity.RefreshToken;
import gifterz.textme.global.security.jwt.JwtUtils;
import gifterz.textme.global.security.service.AesUtils;
import gifterz.textme.global.security.service.RefreshTokenService;
import gifterz.textme.domain.user.dto.request.LoginRequest;
import gifterz.textme.domain.user.dto.request.SignUpRequest;
import gifterz.textme.domain.user.dto.response.LoginResponse;
import gifterz.textme.domain.user.dto.response.UserResponse;
import gifterz.textme.domain.user.entity.Member;
import gifterz.textme.domain.user.entity.User;
import gifterz.textme.domain.user.exception.EmailDuplicatedException;
import gifterz.textme.domain.user.exception.UserNotFoundException;
import gifterz.textme.domain.user.repository.MemberRepository;
import gifterz.textme.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final WebSecurityConfig webSecurityConfig;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AesUtils aesUtils;

    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String name = signUpRequest.getName();
        Optional<User> userExists = userRepository.findByEmail(email);
        checkEmailDuplicated(userExists, email);

        User user = User.of(email, name, AuthType.PASSWORD);
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.of(user, encodedPassword);
        userRepository.save(user);
        memberRepository.save(member);
        String encryptedUserId = encryptUserId(user.getId());
        return new UserResponse(encryptedUserId, user.getName(), user.getEmail());
    }

    private void checkEmailDuplicated(Optional<User> userExists, String email) {
        if (userExists.isPresent()) {
            throw new EmailDuplicatedException(email);
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Member member = memberRepository.findByUser(user).orElseThrow(UserNotFoundException::new);
        checkPassword(password, member);
        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String encryptedUserId = encryptUserId(user.getId());
        return new LoginResponse(encryptedUserId, user.getEmail(), user.getName(), accessToken,
                refreshToken.getRefreshToken(), refreshToken.getCreatedAt());
    }

    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("Invalid Password");
        }
    }

    public UserResponse findUserInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        String encryptedUserId = encryptUserId(user.getId());
        return new UserResponse(encryptedUserId, user.getName(), user.getEmail());
    }

    @Transactional
    public UserResponse updateUserName(String email, String name) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.updateUserName(name);
        String encryptedUserId = encryptUserId(user.getId());
        return new UserResponse(encryptedUserId, user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponse findUserInfoByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        String encryptedUserId = encryptUserId(user.getId());
        return new UserResponse(encryptedUserId, user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponse findUserInfoByUserId(String encryptedId) {
        String userId = aesUtils.decrypt(encryptedId);
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(UserNotFoundException::new);
        return new UserResponse(encryptedId, user.getName(), user.getEmail());
    }

    private String encryptUserId(Long id) {
        String userId = id.toString();
        return aesUtils.encrypt(userId);
    }
}
