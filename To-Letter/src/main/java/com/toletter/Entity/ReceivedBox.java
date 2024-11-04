package com.toletter.Entity;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "received_box")
public class ReceivedBox { // 받은 메일함
    @Schema(description = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Schema(description = "유저 이메일", example = "test@naver.com")
    @Column(nullable = false)
    private String userEmail;

    // 도착할 시간
    @Schema(description = "도착할 시간", example = "2024-03-01T06:06:12")
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime receivedTime;

    @Schema(description = "letter_id(pk)", example = "1")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;
}
