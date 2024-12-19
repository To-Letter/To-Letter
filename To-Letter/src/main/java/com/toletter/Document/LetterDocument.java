package com.toletter.Document;

import com.toletter.DTO.letter.SearchLetterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(indexName = "item")
public class LetterDocument {
    @Id
    private Long id;
    private String toUserEmail;
    private String fromUserEmail;
    private String toUserNickname;
    private String fromUserNickname;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd/HH:mm:ss||epoch_millis")
    private LocalDateTime createdAt;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd/HH:mm:ss||epoch_millis")
    private LocalDateTime arrivedAt;
    private String content;
    private Boolean viewCheck;

    public static LetterDocument from(SearchLetterDTO letter){
        return LetterDocument.builder()
                .id(letter.getId())
                .toUserNickname(letter.getToUserNickname())
                .fromUserNickname(letter.getFromUserNickname())
                .createdAt(letter.getCreatedAt())
                .arrivedAt(letter.getArrivedAt())
                .content(letter.getContents())
                .viewCheck(letter.getViewCheck())
                .build();
    }
}
