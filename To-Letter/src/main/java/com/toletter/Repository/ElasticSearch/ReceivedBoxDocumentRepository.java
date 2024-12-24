package com.toletter.Repository.ElasticSearch;

import com.toletter.Document.ReceivedBoxDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivedBoxDocumentRepository extends ElasticsearchRepository<ReceivedBoxDocument, Long> {
    @Query("{\"bool\": { \"must\": [ { \"match\": { \"userEmail\": \"?0\" } }, { \"nested\": { \"path\": \"letter\", \"query\": { \"bool\": { \"should\": [ { \"wildcard\": { \"letter.fromUserNickname\": \"*?1*\" } }, { \"wildcard\": { \"letter.content\": \"*?2*\" } } ] } } } } ] }}")
    List<ReceivedBoxDocument> findAllByUserEmailAndLetterToUserNicknameOrContentContaining(String userEmail, String keyword, String keyword2);
}
