package gdsc.binaryho.imhere.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int errorCode;
    private final String errorMessage;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
}
