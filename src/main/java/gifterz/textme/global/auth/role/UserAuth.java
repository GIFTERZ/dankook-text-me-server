package gifterz.textme.global.auth.role;

import gifterz.textme.global.security.jwt.AuthorizationExtractor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = AuthorizationExtractor.AUTHORIZATION)
@Secured(UserRole.ROLE_USER)
public @interface UserAuth {
}
