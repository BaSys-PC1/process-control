# Process Control

This repository contains 
- [task-channel-camunda-cc](/modules/task-channel-camunda-cc): a Spring Boot micro service for fetching workload for Control Components in terms of process tasks from the Camunda BPMN platform.
- [cc-task-manager](/modules/cc-task-manager): a Spring Boot micro service for assigning tasks to Control Components and report back the status of performed tasks. The service implements a lightweight capability- and agent-based approach for task assignments.
- [shared-model](/modules/shared-model): the Spring Boot micro services communicate via Apache Kafka. The shared-model contains AVRO definition files for the transfered data objects that are generated into Java classes.
