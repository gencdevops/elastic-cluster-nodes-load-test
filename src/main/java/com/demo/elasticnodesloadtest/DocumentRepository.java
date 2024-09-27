package com.demo.elasticnodesloadtest;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentRepository extends ElasticsearchRepository<DocumentEntity, String> {
}
