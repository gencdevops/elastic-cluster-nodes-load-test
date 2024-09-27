# Elasticsearch Node Performance Test Application

This project is a Spring Boot-based Java application designed to test the performance of nodes in an Elasticsearch cluster. The application performs read and write operations to the specified nodes and measures response times.

## Features

- Tests the performance of nodes in an Elasticsearch cluster.
- Measures response times by writing and reading documents from a specific node.
- Automatically refreshes Elasticsearch indices to ensure up-to-date data is retrieved.

## Requirements

- Java 17+
- Maven 3.6+
- Elasticsearch 7.x (running locally on localhost)
- Spring Boot 3.3.4