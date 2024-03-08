package gdsc.binaryho.imhere.core.member;

public enum Role {
    GUEST("ROLE_GUEST"),
    ADMIN("ROLE_ADMIN"),
    LECTURER("ROLE_LECTURER"),
    STUDENT("ROLE_STUDENT"),

    ;

    private final String key;

    Role(String role) {
        this.key = role;
    }

    public String getKey() {
        return key;
    }
}
