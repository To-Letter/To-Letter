package com.toletter.Repository.ElasticSearch;

import com.toletter.Document.ReceivedBoxDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivedBoxDocumentRepository extends ElasticsearchRepository<ReceivedBoxDocument, Long> {

}
