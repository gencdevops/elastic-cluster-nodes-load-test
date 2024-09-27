package com.demo.elasticnodesloadtest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElasticSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);


    private final ElasticSearchService elasticSearchService;

    @GetMapping("/test-elasticsearch")
    public String testElasticSearchNodes() {
        logger.info("Starting Elasticsearch node performance tests...");

        elasticSearchService.testNodePerformance();

        logger.info("Elasticsearch node performance test completed.");
        return "Elasticsearch node performance test completed.";
    }
}