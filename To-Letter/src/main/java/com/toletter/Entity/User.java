package com.toletter.Entity;

import com.toletter.Enums.LoginType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자를 자동으로 추가.
@AllArgsConstructor // 필드 값을 파라미터로 받는 생성자 추가.
@Table(name = "user")
public class User {
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    // 로그인타입
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    // 2차 인증 확인
    @Column(nullable = false)
    private boolean secondConfirmed;
}
