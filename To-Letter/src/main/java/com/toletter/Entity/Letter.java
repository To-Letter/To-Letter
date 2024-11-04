package com.toletter.Entity;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "letter")
public class Letter {
    @Schema(description = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Schema(description = "받는 유저 이메일", example = "to_test@naver.com")
    private String toUserEmail;

    @Schema(description = "보내는 유저 이메일", example = "from_test@naver.com")
    @Column(nullable = false)
    private String fromUserEmail;

    @Schema(description = "받는 유저 닉네임", example = "to_test")
    private String toUserNickname;

    @Schema(description = "보내는 유저 닉네임", example = "from_test")
    @Column(nullable = false)
    private String fromUserNickname;

    // 메일 보낸 시간
    @Schema(description = "메일 보낸 시간", example = "2024-03-01T06:06:12")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime createdAt;

    // 메일 도착할 시간
    @Schema(description = "메일 도착할 시간", example = "2024-03-04T06:06:12")
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime arrivedAt;

    @Schema(description = "내용", example = "안녕? 나는 조교야! 우리 친하게 지내자!")
    private String contents;

    @Schema(description = "메일 읽었는지 확인", example = "T / F")
    private Boolean viewCheck;

    public void updateViewCheck(){
        this.viewCheck = true;
    }
}
