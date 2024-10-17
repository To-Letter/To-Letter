package com.toletter.Entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(value = "받는 유저 이메일", example = "to_test@naver.com")
    private String toUserEmail;

    @ApiModelProperty(value = "보내는 유저 이메일", example = "from_test@naver.com")
    @Column(nullable = false)
    private String fromUserEmail;

    // 메일 보낸 시간
    @ApiModelProperty(value = "메일 보낸 시간", example = "2024-03-01T06:06:12")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime createdAt;

    // 메일 도착할 시간
    @ApiModelProperty(value = "메일 도착할 시간", example = "2024-03-04T06:06:12")
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime arrivedAt;

    @ApiModelProperty(value = "제목", example = "IT학부즈 보아라.")
    private String title;

    @ApiModelProperty(value = "내용", example = "안녕? 나는 조교야! 우리 친하게 지내자!")
    private String contents;

    @ApiModelProperty(value = "메일 읽었는지 확인", example = "T / F")
    private Boolean viewCheck;

    public void updateViewCheck(){
        this.viewCheck = true;
    }
}
