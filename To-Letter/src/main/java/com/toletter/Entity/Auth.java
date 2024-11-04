package com.toletter.Entity;

import javax.persistence.*;

import com.toletter.Enums.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor // 기본 생성자를 만들어줌.
@AllArgsConstructor
@Builder
@Table(name = "auth")
public class Auth {
    @Schema(description = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Schema(description = "이메일(2차인증 시 사용)", example = "test@gmail.com")
    @Column
    private String email;

    @Schema(description = "2차 인증 보낸 시간", example = "2024-03-01T06:06:12")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "랜덤코드", example = "a2d1e3")
    @Column
    private String randomCode;

    @Schema(description = "메일 구분(2차 인증 / 비밀번호 변경)", example = "secondAuth / updatePW")
    @Column
    private AuthType authType;
}
