package com.toletter.Entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "received_box")
public class ReceivedBox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String user_id;

    // 도착한 시간
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime receivedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;
}
