package com.toletter.Document;

import com.toletter.Entity.SentBox;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(indexName = "sentbox")
public class SentBoxDocument {
    @Id
    private Long id;
    private String userEmail;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd/HH:mm:ss||epoch_millis")
    private LocalDateTime sentTime;
    @Field(type = FieldType.Nested, includeInParent = true)
    private LetterDocument letter;


    public static SentBoxDocument from(SentBox sentBox, LetterDocument letter){
        return SentBoxDocument.builder()
                .id(sentBox.getId())
                .userEmail(sentBox.getUserEmail())
                .sentTime(sentBox.getSentTime())
                .letter(letter)
                .build();
    }
}
