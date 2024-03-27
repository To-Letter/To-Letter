package com.toletter.Enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum LoginType {
    localLogin,
    kakaoLogin;
}