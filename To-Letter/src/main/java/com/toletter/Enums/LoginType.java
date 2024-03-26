package com.toletter.Enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum LoginType {
    localLogin("ROLE_LOCAL"),
    kakaoLogin("ROLE_KAKAO");

    private final String value;
}