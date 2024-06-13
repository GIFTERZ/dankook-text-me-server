package gifterz.textme.global.auth.aspect;

import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.error.exception.NoAuthorizationException;
import gifterz.textme.global.auth.role.UserRole;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthTypeCheckAspect {

    @Before("gifterz.textme.global.auth.pointcut.Pointcuts.dkuAuth()")
    public void checkDkuAuth(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof JwtAuthentication authentication) {
                if (hasDkuAuth(authentication.getUserRole(), authentication.getAuthType())) return;
            }
        }
        throw new NoAuthorizationException("DKU 로그인 시에만 접근 가능합니다.");
    }

    private static boolean hasDkuAuth(UserRole userRole, AuthType authType) {
        if (userRole == UserRole.USER || userRole == UserRole.ADMIN) {
            return authType == AuthType.DKU;
        }
        return false;
    }
}
