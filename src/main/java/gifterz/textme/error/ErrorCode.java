package gifterz.textme.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {
    // -----예외(Http 상태, 에러코드, 메시지)------ //

    // Common 공통 예외
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력입니다."),
    ACCESS_DENIED(400, "C002", "접근이 제한되었습니다."),
    RESOURCE_NOT_FOUND(204, "C003", "자원을 찾지 못했습니다."),
    ENTITY_NOT_FOUND(400, "C004", "엔티티를 찾지 못했습니다."),
    NAME_EMPTY(400, "C005", "이름이 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "C006", "서버에 문제가 발생했습니다."),
    FORBIDDEN_USER(403, "C007", "해당 요청에 대한 권한이 없습니다."),
    TooManyRequestException(429, "C008", "너무 많은 요청입니다. 30초 후에 다시 시도해주세요."),

    // USER
    EMAIL_ALREADY_TAKEN(400, "M001", "이미 존재하는 이메일입니다."),
    TOKEN_EXPIRED(401, "M002", "토큰이 만료되었습니다."),
    INVALID_TOKEN(400, "M003", "잘못된 토큰입니다."),
    USER_NAME_NOT_FOUND(400, "M004", "해당 이름의 사용자를 찾을수 없습니다."),
    EMAIL_USER_NOT_FOUND(400, "M005", "해당 이메일의 사용자가 존재하지 않습니다."),
    TOKEN_NOT_EXIST(400, "M006", "토큰을 입력해 주세요."),
    EMAIL_EMPTY(400, "M007", "이메일을 입력해주세요."),
    PASSWORD_EMPTY(400, "M008", "비밀번호를 입력해주세요."),
    INVALID_LOGIN_INFO(400, "M009", "이메일 또는 비밀번호를 다시 확인해 주세요."),
    UNAUTHORIZED_USER(401, "M010", "로그인 후 이용가능합니다."),
    USER_EMPTY(400, "M011", "존재하지 않는 사용자입니다."),
    LOGIN_AGAIN(400, "M012", "로그인을 다시 시도해 주세요"),

    // Letter
    LETTER_EMPTY(400, "L001", "해당 편지가 없습니다."),

    // Status
    STATUS_EMPTY(400, "S001", "상태를 입력해주세요."),

    // File
    INVALID_FILE_CONTENT(400, "F001", "적절하지 않은 파일 형식입니다."),
    Illegal_FILE(400, "F002", "업로드 할 수 없는 이미지입니다."),
    FAIL_FILE_RESIZE(400, "F003", "파일 리사이즈에 실패했습니다."),
    FILE_TOO_LARGE(413, "F004", "파일이 너무 큽니다."),
    FILE_UPLOAD_ERROR(500, "F005", "파일 업로드에 실패했습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(final int httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
