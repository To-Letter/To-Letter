package com.toletter.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "letter")
public class Letter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String toUserId;

    @Column(nullable = false)
    private String fromUserId;

    // 메일 보낸 시간
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime createdAt;

    // 메일 도착할 시간
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime arrivedAt;

    private String title;

    private String contents;

    private Boolean viewCheck;

    @Column(nullable = false)
    private Boolean temporaryStorage;
}
