package gifterz.textme.global.auth.aspect;

import gifterz.textme.error.exception.NoAuthorizationException;
import gifterz.textme.global.auth.role.UserRole;
import gifterz.textme.global.security.jwt.JwtAuthentication;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleCheckAspect {

    @Before("gifterz.textme.global.auth.pointcut.Pointcuts.userAuth()")
    public void checkUserAuth(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof JwtAuthentication authentication) {
                if (hasAuthority(authentication.getUserRole())) {
                    return;
                }
                throw new NoAuthorizationException("사용자 권한이 없습니다.");
            }
        }
        throw new NoAuthorizationException("사용자 권한이 없습니다.");
    }

    private static boolean hasAuthority(UserRole userRole) {
        return userRole == UserRole.USER || userRole == UserRole.ADMIN;
    }

    @Before("gifterz.textme.global.auth.pointcut.Pointcuts.adminAuth()")
    public void checkAdminAuth(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof JwtAuthentication authentication) {
                if (hasAdminAuth(authentication.getUserRole())) {
                    return;
                }
                throw new NoAuthorizationException("관리자 권한이 없습니다.");
            }
        }
        throw new NoAuthorizationException("관리자 권한이 없습니다.");
    }

    private static boolean hasAdminAuth(UserRole userRole) {
        return userRole == UserRole.ADMIN;
    }
}
