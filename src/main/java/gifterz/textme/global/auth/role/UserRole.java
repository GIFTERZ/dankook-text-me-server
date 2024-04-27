package gifterz.textme.global.auth.role;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}
