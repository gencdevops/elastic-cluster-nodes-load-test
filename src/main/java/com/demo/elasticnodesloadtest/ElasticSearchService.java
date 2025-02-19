package com.demo.elasticnodesloadtest;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private final ElasticsearchOperations elasticsearchOperations;

    private final String[] nodes = {
            "nodes url"
    };

    public void testNodePerformance() {
        for (String node : nodes) {
            testNodePerformance(node);
        }
    }

    private void testNodePerformance(String nodeUrl) {
        int numberOfDocuments = 10; // Number of documents to write/read for load testing
        long totalWriteTime;
        long totalReadTime;

        List<String> documentIds = new ArrayList<>(); // Store document IDs for reading later

        totalWriteTime = performWriteOperations(nodeUrl, numberOfDocuments, documentIds);
        totalReadTime = performReadOperations(nodeUrl, documentIds);
        logTotalTimes(nodeUrl, numberOfDocuments, totalWriteTime, totalReadTime);
    }

    private long performWriteOperations(String nodeUrl, int numberOfDocuments, List<String> documentIds) {
        long[] totalWriteTime = {0}; // Use an array to hold the total write time

        IntStream.range(0, numberOfDocuments).forEach(i -> {
            String documentId = String.valueOf(Instant.now().toEpochMilli() + i); // Unique ID to avoid cache hits
            documentIds.add(documentId); // Store the ID for later reading

            DocumentEntity document = new DocumentEntity(documentId, "Test Message " + i, Instant.now().toEpochMilli());

            long startTime = System.currentTimeMillis();

            // Index the document
            elasticsearchOperations.save(document, IndexCoordinates.of("test_index"));
            refreshIndex("test_index");  // Force refresh after writing

            long writeTime = System.currentTimeMillis() - startTime;
            totalWriteTime[0] += writeTime; // Update the total write time

            logger.info("Node: {} - Write Response Time for Document ID {}: {} ms", nodeUrl, documentId, writeTime);
        });

        return totalWriteTime[0];
    }

    private long performReadOperations(String nodeUrl, List<String> documentIds) {
        long totalReadTime = 0;

        for (String documentId : documentIds) { // Use stored document IDs

            long startTime = System.currentTimeMillis();

            // Fetch the document using CriteriaQuery for ID-based search
            Criteria criteria = Criteria.where("id").is(documentId);
            CriteriaQuery query = new CriteriaQuery(criteria);

            List<DocumentEntity> fetchedDocuments = elasticsearchOperations.search(query, DocumentEntity.class, IndexCoordinates.of("test_index"))
                    .stream()
                    .map(hit -> hit.getContent())
                    .toList();

            long readTime = System.currentTimeMillis() - startTime;
            totalReadTime += readTime;

            if (!fetchedDocuments.isEmpty()) {
                logger.info("Node: {} - Document ID {} retrieved - Read Response Time: {} ms", nodeUrl, documentId, readTime);
            } else {
                logger.info("Node: {} - Document ID {} not found - Read Response Time: {} ms", nodeUrl, documentId, readTime);
            }
        }

        return totalReadTime;
    }

    private void logTotalTimes(String nodeUrl, int numberOfDocuments, long totalWriteTime, long totalReadTime) {
        logger.info("Node: {} - Total Write Time for {} documents: {} ms", nodeUrl, numberOfDocuments, totalWriteTime);
        logger.info("Node: {} - Total Read Time for {} documents: {} ms", nodeUrl, numberOfDocuments, totalReadTime);
    }

    private void refreshIndex(String indexName) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
        indexOps.refresh();  // Forces a refresh of the index to make sure written documents are immediately available for search
    }
}