package gifterz.textme.global.config;

import gifterz.textme.global.auth.role.UserRole;
import gifterz.textme.global.security.jwt.AuthEntryPointJwt;
import gifterz.textme.global.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthEntryPointJwt authEntryPointJwt;
    private static final String[] PUBLIC_URI = {
            "/users/**", "/letters/**", "/files/**", "/cards/**", "/oauth/**",
            "/swagger-ui/**", "/swagger-resources/**",
            "/api-docs/**", "/error/**"
    };

    private static final String[] ADMIN_URI = {
            "/admin/**"
    };
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ehconfigurer -> ehconfigurer.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_URI)
                        .permitAll()
                        .requestMatchers(ADMIN_URI).hasAuthority(UserRole.ROLE_ADMIN)
                        .anyRequest().authenticated())
                .build();
    }

}
