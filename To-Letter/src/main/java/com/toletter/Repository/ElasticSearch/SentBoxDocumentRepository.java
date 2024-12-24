package com.toletter.Repository.ElasticSearch;

import com.toletter.Document.SentBoxDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentBoxDocumentRepository extends ElasticsearchRepository<SentBoxDocument, Long> {
    @Query("{\"bool\": { \"must\": [ { \"match\": { \"userEmail\": \"?0\" } }, { \"nested\": { \"path\": \"letter\", \"query\": { \"bool\": { \"should\": [ { \"wildcard\": { \"letter.toUserNickname\": \"*?1*\" } }, { \"wildcard\": { \"letter.content\": \"*?2*\" } } ] } } } } ] }}")
    List<SentBoxDocument> findAllByLetterContentContainingOrLetterToUserNicknameContainingOrderBySentTimeDesc(String userEmail, String keyword, String keyword2);
}
