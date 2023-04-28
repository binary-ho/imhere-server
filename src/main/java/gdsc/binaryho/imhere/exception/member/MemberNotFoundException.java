package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class MemberNotFoundException extends ImhereException {

    public static final ImhereException EXCEPTION = new MemberNotFoundException();

    private MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
