package gifterz.textme.domain.security;

import gifterz.textme.domain.oauth.util.OauthServerTypeConverter;
import gifterz.textme.domain.security.jwt.JwtAuthArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(jwtAuthArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PATCH")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000",
                        "http://192.168.0.82:3000", "https://next-app-dot-encoded-mark-411805.du.r.appspot.com/")
                .allowCredentials(true);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OauthServerTypeConverter());
    }
}
