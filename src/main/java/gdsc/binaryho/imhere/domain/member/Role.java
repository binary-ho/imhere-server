package gdsc.binaryho.imhere.domain.member;

public enum Role {
    ADMIN("ROLE_ADMIN"), LECTURER("ROLE_LECTURER"), STUDENT("ROLE_STUDENT");

    private final String key;

    Role(String role) {
        this.key = role;
    }

    protected String getKey() {
        return key;
    }
}
