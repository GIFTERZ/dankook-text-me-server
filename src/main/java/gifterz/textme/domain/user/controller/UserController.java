package gifterz.textme.domain.user.controller;

import gifterz.textme.common.firebase.FCMService;
import gifterz.textme.domain.security.jwt.JwtAuth;
import gifterz.textme.domain.security.service.RefreshTokenService;
import gifterz.textme.domain.user.dto.request.LoginRequest;
import gifterz.textme.domain.user.dto.request.SignUpRequest;
import gifterz.textme.domain.user.dto.request.TokenRefreshRequest;
import gifterz.textme.domain.user.dto.response.LoginResponse;
import gifterz.textme.domain.user.dto.response.TokenRefreshResponse;
import gifterz.textme.domain.user.dto.response.UserResponse;
import gifterz.textme.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final FCMService fcmService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid final SignUpRequest request) {
        UserResponse userResponse = userService.signUp(request);
        return ResponseEntity.ok().body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
//        fcmService.saveToken(request);
        return ResponseEntity.ok().body(loginResponse);
    }

    @DeleteMapping("/logout")
    public void logout(@JwtAuth String email) {
        fcmService.deleteToken(email);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody @Valid final TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshTokens(request.getRefreshToken());
        return ResponseEntity.ok().body(tokenRefreshResponse);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(@JwtAuth String email) {
        UserResponse userResponse = userService.findUserInfo(email);
        return ResponseEntity.ok().body(userResponse);
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateUserName(@JwtAuth String email, @RequestParam final String name) {
        UserResponse userResponse = userService.updateUserName(email, name);
        return ResponseEntity.ok().body(userResponse);
    }

    @GetMapping("/find")
    public ResponseEntity<UserResponse> findUserInfoByEmail(@RequestParam final String email) {
        UserResponse userResponse = userService.findUserInfoByEmail(email);
        return ResponseEntity.ok().body(userResponse);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserResponse> findUserInfoByUserId(@PathVariable("id") final String id) {
        UserResponse userResponse = userService.findUserInfoByUserId(id);
        return ResponseEntity.ok().body(userResponse);
    }
}
