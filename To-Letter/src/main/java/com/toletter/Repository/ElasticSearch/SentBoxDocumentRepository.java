package com.toletter.Repository.ElasticSearch;

import com.toletter.Document.SentBoxDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentBoxDocumentRepository extends ElasticsearchRepository<SentBoxDocument, Long> {

}
