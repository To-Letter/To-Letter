package com.toletter.Enums;

import lombok.Getter;

@Getter
public enum JwtErrorCode {
    INVALID_TOKEN(1001, "유효하지 않은 토큰입니다."),
    WRONG_TYPE_TOKEN(1002, "빈 문자열 토큰입니다."),
    EXPIRED_TOKEN(1003, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(1004, "변조된 토큰입니다."),
    WRONG_TOKEN(1005, "잘못된 접근입니다.");


    JwtErrorCode (int status, String message) {
        this.code = status;
        this.message = message;
    }

    private int code;
    private String message;
}
