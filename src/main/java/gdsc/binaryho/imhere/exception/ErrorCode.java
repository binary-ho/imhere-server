package gdsc.binaryho.imhere.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    BAD_REQUEST(400, "잘못된 요청입니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "Not Found"),

    MEMBER_NOT_FOUND(440, "로그인 회원 정보가 없습니다."),
    PASSWORD_INCORRECT(441, "로그인 회원 비밀번호가 불일치합니다."),
    EMAIL_DUPLICATED(443, "이미 가입된 Email 입니다."),
    EMAIL_FORMAT_MISMATCH(445, "형식에 맞는 이메일을 입력하세요."),
    PASSWORD_FORMAT_MISMATCH(446, "형식에 맞는 비밀번호를 입력하세요."),
    REQUEST_FORMAT_MISMATCH(447, "Request 형식이 맞지 않습니다."),

    LECTURE_NOT_FOUND(450, "강의 정보가 없습니다."),
    LECTURE_NOT_OPEN(451, "강의에 출석 가능한 상태가 아닙니다. (Lecture Not OPEN)"),

    ENROLLMENT_NOT_FOUND(461, "수강신청 정보 없음"),
    ENROLLMENT_DUPLICATED(462, "수강신청 중복 요청"),
    ENROLLMENT_NOT_APPROVED(463, "수강신청 승인되지 않음"),

    ATTENDANCE_NUMBER_INCORRECT(470, "출석 번호 불일치"),
    ATTENDANCE_TIME_EXCEEDED(471, "출석 가능 시간 초과"),

    REQUEST_MEMBER_ID_MISMATCH(490, "실제로 요청을 보낸 유저의 id와 Request에 기재된 id가 다릅니다. (악의적 요청)"),
    PERMISSION_DENIED(491, "유저가 권한 이상의 Request를 보냈습니다. (악의적 요청)"),

    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
