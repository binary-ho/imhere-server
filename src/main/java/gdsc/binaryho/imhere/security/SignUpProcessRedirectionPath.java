package gdsc.binaryho.imhere.security;

import gdsc.binaryho.imhere.core.member.Member;
import lombok.Getter;

@Getter
public enum SignUpProcessRedirectionPath {

    BEFORE_MEMBER_INFO_INPUT("/signup"),
    SIGN_UP_DONE("/main"),
    ;

    private final String redirectUrlPath;

    public static SignUpProcessRedirectionPath of(Member member) {
        if (member.getName() == null) {
            return BEFORE_MEMBER_INFO_INPUT;
        }

        return SIGN_UP_DONE;
    }

    SignUpProcessRedirectionPath(String redirectUrlPath) {
        this.redirectUrlPath = redirectUrlPath;
    }
}
