package com.toletter.Entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자를 만들어줌.
@Table(name = "auth")
public class Auth {
    @ApiModelProperty(value = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(value = "전화번호(2차인증 시 사용)", example = "01012345678")
    @Column
    private String phoneNumber;

    @ApiModelProperty(value = "2차 인증 보낸 시간", example = "2024-03-01/06:06:12")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "랜덤코드", example = "a2d1e3")
    @Column
    private String randomCode;
}
