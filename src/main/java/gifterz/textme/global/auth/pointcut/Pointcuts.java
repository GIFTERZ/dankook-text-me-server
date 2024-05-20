package gifterz.textme.global.auth.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("@annotation(gifterz.textme.global.auth.role.UserAuth)")
    public void userAuth() {
    }

    @Pointcut("@annotation(gifterz.textme.global.auth.role.AdminAuth)")
    public void adminAuth() {
    }

    @Pointcut("userAuth() || adminAuth()")
    public void userOrAdminAuth() {
    }

    @Pointcut("@annotation(gifterz.textme.global.auth.role.DkuAuth)")
    public void dkuAuth() {
    }

    @Pointcut("dkuAuth() && userOrAdminAuth()")
    public void eventAndUserOrAdminAuth() {
    }

}
