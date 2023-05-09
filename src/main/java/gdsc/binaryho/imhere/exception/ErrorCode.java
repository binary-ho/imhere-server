package gdsc.binaryho.imhere.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 440, "로그인 회원 정보가 없습니다."),
    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, 441, "로그인 회원 비밀번호가 불일치합니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, 443, "이미 가입된 Email 입니다."),
    EMAIL_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 445, "형식에 맞는 이메일을 입력하세요."),
    PASSWORD_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 446, "형식에 맞는 비밀번호를 입력하세요."),
    REQUEST_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 447, "요청 형식이 맞지 않습니다."),

    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, 450, "강의 정보가 없습니다."),
    LECTURE_NOT_OPEN(HttpStatus.FORBIDDEN, 451, "강의에 출석 가능한 상태가 아닙니다. (Lecture Not OPEN)"),

    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 461, "수강신청 정보 없음"),
    ENROLLMENT_DUPLICATED(HttpStatus.CONFLICT, 462, "수강신청 중복 요청"),
    ENROLLMENT_NOT_APPROVED(HttpStatus.FORBIDDEN, 463, "수강신청 승인되지 않음"),

    ATTENDANCE_NUMBER_INCORRECT(HttpStatus.BAD_REQUEST, 470, "출석 번호 불일치"),
    ATTENDANCE_TIME_EXCEEDED(HttpStatus.BAD_REQUEST, 471, "출석 가능 시간 초과"),

    REQUEST_MEMBER_ID_MISMATCH(HttpStatus.BAD_REQUEST, 490, "실제로 요청을 보낸 유저의 id와 Request에 기재된 id가 다릅니다. (악의적 요청)"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, 491, "유저가 권한이 없는 요청을 보냈습니다. (악의적 요청)"),

    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
