package com.toletter.Entity;

import com.toletter.Enums.LoginType;
import javax.persistence.*;

import com.toletter.Enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자를 자동으로 추가.
@AllArgsConstructor // 필드 값을 파라미터로 받는 생성자 추가.
@Table(name = "user")
public class User {

    @ApiModelProperty(value = "아이디(메일보낼 때 사용)", example = "test")
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @ApiModelProperty(value = "비밀번호(암호화)", example = "testPW")
    @Column(nullable = false)
    private String password;

    @ApiModelProperty(value = "닉네임", example = "testNickname")
    @Column(unique = true, nullable = false)
    private String nickname;

    @ApiModelProperty(value = "주소", example = "37.343645200551194, 126.95377470484397")
    @Column(nullable = false)
    private String address;

    @ApiModelProperty(value = "전화번호(2차인증 시 사용)", example = "01012345678")
    @Column(nullable = false)
    private String phoneNumber;

    // 로그인타입
    @ApiModelProperty(value = "로그인 타입", example = "local/kakao")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    // 유저 권한 확인
    @ApiModelProperty(value = "유저 권한", example = "admin/user")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    // 2차 인증 확인
    @ApiModelProperty(value = "2차 인증 확인", example = "T / F")
    @Column(nullable = false)
    private boolean secondConfirmed;
}
