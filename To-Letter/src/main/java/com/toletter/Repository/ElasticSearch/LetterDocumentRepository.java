package com.toletter.Repository.ElasticSearch;

import com.toletter.Document.LetterDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterDocumentRepository extends ElasticsearchRepository<LetterDocument, Long> {

}
