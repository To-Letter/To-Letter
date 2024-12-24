package com.toletter.Document;

import com.toletter.Entity.ReceivedBox;
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
@Document(indexName = "receivedbox")
public class ReceivedBoxDocument {
    @Id
    private Long id;
    private String userEmail;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd/HH:mm:ss||epoch_millis")
    private LocalDateTime receivedTime;
    @Field(type = FieldType.Nested, includeInParent = true)
    private LetterDocument letter;


    public static ReceivedBoxDocument from(ReceivedBox receivedBox, LetterDocument letter){
        return ReceivedBoxDocument.builder()
                .id(receivedBox.getId())
                .userEmail(receivedBox.getUserEmail())
                .receivedTime(receivedBox.getReceivedTime())
                .letter(letter)
                .build();
    }
}
