package com.toletter.Entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "received_box")
public class ReceivedBox { // 받은 메일함
    @ApiModelProperty(value = "ID(자동)", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(value = "유저 아이디", example = "test")
    @Column(nullable = false)
    private String user_id;

    // 도착할 시간
    @ApiModelProperty(value = "도착할 시간", example = "2024-03-01T06:06:12")
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime receivedTime;

    @ApiModelProperty(value = "letter_id(pk)", example = "1")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;
}
