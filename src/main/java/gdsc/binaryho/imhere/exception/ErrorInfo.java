package gdsc.binaryho.imhere.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorInfo {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "로그인 회원 정보가 없습니다."),
    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, 1002, "로그인 회원 비밀번호가 불일치합니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, 1003, "이미 가입된 Email 입니다."),
    EMAIL_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 1004, "형식에 맞는 이메일을 입력하세요."),
    PASSWORD_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 1005, "형식에 맞는 비밀번호를 입력하세요."),
    REQUEST_FORMAT_MISMATCH(HttpStatus.BAD_REQUEST, 1006, "요청 형식이 맞지 않습니다."),
    EMAIL_VERIFICATION_CODE_INCORRECT(HttpStatus.BAD_REQUEST, 1007, "Email 인증 번호가 불일치합니다."),
    MESSAGING_SERVER_EXCEPTION(HttpStatus.NOT_FOUND, 1008, "Messaging Server 에 연결할 수 없습니다."),
    PASSWORD_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, 1009, "비밀번호 변경 요청시 보내온 비밀번호가 비어 있습니다."),
    PASSWORDS_NOT_EQUAL(HttpStatus.BAD_REQUEST, 1010, "비밀번호 변경 요청시 보내온 새 비밀번호와 확인용 비밀번호가 불일치합니다."),
    PASSWORD_CHANGE_MEMBER_NOT_EXIST(HttpStatus.NOT_FOUND, 1011, "비밀번호 변경을 시도한 이메일을 소유한 회원이 없습니다."),

    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "강의 정보가 없습니다."),
    LECTURE_NOT_OPEN(HttpStatus.FORBIDDEN, 2002, "강의에 출석 가능한 상태가 아닙니다. (Lecture Not OPEN)"),

    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "수강신청 정보 없음"),
    ENROLLMENT_DUPLICATED(HttpStatus.CONFLICT, 3002, "수강신청 중복 요청"),
    ENROLLMENT_NOT_APPROVED(HttpStatus.FORBIDDEN, 3003, "수강신청 승인되지 않음"),

    ATTENDANCE_NUMBER_INCORRECT(HttpStatus.BAD_REQUEST, 4001, "출석 번호 불일치"),
    ATTENDANCE_TIME_EXCEEDED(HttpStatus.BAD_REQUEST, 4002, "출석 가능 시간 초과"),

    REQUEST_MEMBER_ID_MISMATCH(HttpStatus.BAD_REQUEST, 9001, "실제로 요청을 보낸 유저의 id와 Request에 기재된 id가 다릅니다. (악의적 요청)"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, 9002, "유저가 권한이 없는 요청을 보냈습니다. (악의적 요청)"),
    UNEXPECTED_REDIS_DATA_TYPE(HttpStatus.NOT_FOUND, 9003, "수강생 정보에 이상이 있어 캐싱할 수 없습니다. (문의 주세요)"),

    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
