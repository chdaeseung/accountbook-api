package chdaeseung.accountbook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인 해주세요."),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래내역이 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "사용중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "사용중인 이메일입니다."),
    BLANK_CATEGORY_NAME(HttpStatus.CONFLICT, "카테고리 이름을 입력해주세요."),
    CHOOSE_CATEGORY(HttpStatus.CONFLICT, "카테고리를 선택해주세요"),
    CATEGORY_IN_USE(HttpStatus.CONFLICT, "사용중인 카테고리는 삭제할 수 없습니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "이미 등록된 카테코리입니다."),
    INCORRECT_ACCOUNT(HttpStatus.CONFLICT, "아이디 또는 비밀번호가 일치하지 않습니다."),
    CANT_BE_END_DATE(HttpStatus.CONFLICT, "시작일이 종료일보다 이후일 수 없습니다."),
    RECURRING_NOT_FOND(HttpStatus.NOT_FOUND, "정기 지출을 찾을 수 없습니다."),
    MINIMUM_AMOUNT(HttpStatus.CONFLICT, "금액은 0보다 커야합니다."),
    INCORRECT_DAY(HttpStatus.CONFLICT, "반복일을 1일부터 31일 사이로 입력해주세요."),
    INPUT_START_DAY(HttpStatus.CONFLICT, "시작일을 입력해주세요."),
    INSERT_DATE(HttpStatus.CONFLICT, "날짜를 입력해주세요."),
    CHOOSE_TYPE(HttpStatus.CONFLICT, "거래 유형을 선택해주세요."),
    CHOOSE_EXPENSE_TYPE(HttpStatus.CONFLICT, "지출 유형을 선택해주세요."),
    CHOOSE_ACCOUNT(HttpStatus.CONFLICT, "계좌를 선택해주세요"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."),
    SAME_ACCOUNT_ID(HttpStatus.CONFLICT, "출금 계좌와 입금 계좌가 같습니다."),
    INSUFFICIENT_BALANCE(HttpStatus.CONFLICT, "잔액이 부족합니다."),
    PASSWORD_MISMATCH(HttpStatus.CONFLICT, "비밀번호가 일치하지 않습니다.");



    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
