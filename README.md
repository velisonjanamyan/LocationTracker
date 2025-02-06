# Location Tracking Application

This application is a Spring Boot application that consumes Location objects from a Kafka topic, calculates the traveled distance per person, and prints the updated total distance to the console.

---

## 1. Overview

This application listens to messages published on a Kafka topic (`person-locations`). Each message contains location data for a specific person identified by personId. The consumer:

1. Stores each location event.
2. Calculates the distance traveled by that person based on previously recorded latitude/longitude.
3. Prints the updated total distance to the console.

---

## 2. Prerequisites

Before running the application, ensure that the following are installed:

- Java 17 or higher
- Maven 3.x
- Apache Kafka (with Zookeeper)
- Git (optional, for cloning the repository)

Verify installations:


java -version
mvn -version


---

## 3. Installation and Build

### Clone the Repository


git clone <repository-url>
cd location-tracking


### Build the Project


mvn clean install


This command:

- Downloads dependencies
- Compiles Java classes
- Packages the application into target/location-tracking-0.0.1-SNAPSHOT.jar

---

## 4. Kafka Setup

### Start Zookeeper


bin/zookeeper-server-start.sh config/zookeeper.properties


### Start Kafka Broker


bin/kafka-server-start.sh config/server.properties


### Create Kafka Topic


bin/kafka-topics.sh --create --topic person-locations --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1


---

## 5. Running the Application

### Run via Command Line


java -jar target/location-tracking-0.0.1-SNAPSHOT.jar


### Run via IDE

1. Import the project as a Maven project.
2. Locate and run LocationTrackingApplication class (contains main method).

---

## 6. Project Structure
```

location-tracking
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── locationtracking
│   │   │               ├── config
│   │   │               │   ├── KafkaConsumerConfig.java
│   │   │               │   └── KafkaProducerConfig.java
│   │   │               ├── LocationTrackingApplication.java
│   │   │               ├── model
│   │   │               │   └── Location.java
│   │   │               └── service
│   │   │                   ├── LocationConsumer.java
│   │   │                   ├── LocationProducer.java
│   │   │                   └── ReportService.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
└── target
    ├── classes
    │   ├── application.properties
    │   └── com
    │       └── example
    │           └── locationtracking
    │               ├── config
    │               │   ├── KafkaConsumerConfig.class
    │               │   └── KafkaProducerConfig.class
    │               ├── LocationTrackingApplication.class
    │               ├── model
    │               │   └── Location.class
    │               └── service
    │                   ├── LocationConsumer.class
    │                   ├── LocationProducer.class
    │                   └── ReportService.class
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    ├── location-tracking-0.0.1-SNAPSHOT.jar
    ├── location-tracking-0.0.1-SNAPSHOT.jar.original
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       ├── compile
    │       │   └── default-compile
    │       │       ├── createdFiles.lst
    │       │       └── inputFiles.lst
    │       └── testCompile
    │           └── default-testCompile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    └── test-classes
```
## 7. Configuration Details

`application.properties`


spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=location_group
spring.kafka.consumer.auto-offset-reset=earliest


Adjust settings based on your Kafka setup.

---

## 8. Usage and Explanation

### Application Flow

1. KafkaProducerConfig

   - Configures producer properties for JSON serialization.
   - Defines producerFactory and kafkaTemplate.

2. LocationConsumer

   - Subscribes to person-locations Kafka topic.
   - Stores received Location events.
   - Calculates total distance traveled.
   - Prints updates to the console:
     
     Received update from user {personId} at yyyy/MM/dd/HH:mm:ss -> total distance = {X.XXXX} km
     

3. Location Model

   - Contains personId, latitude, longitude, and timestamp.
   - Uses LocalDateTime for timestamps.

4. LocationTrackingApplication

   - Entry point for running the application.
   - Uses @EnableScheduling.

### Producing Test Messages

Use Kafka console producer:


bin/kafka-console-producer.sh --broker-list localhost:9092 --topic person-locations


Then type JSON messages:

Narek Hovhannisyan, [06.02.25 19:03]
{"personId":"john123","latitude":37.7749,"longitude":-122.4194,"timestamp":"2025-02-06T10:15:30"}


---

## 9. Troubleshooting

### Consumer Not Receiving Messages

- Ensure spring.kafka.bootstrap-servers is correct.
- Verify Kafka topic exists (`bin/kafka-topics.sh --list --bootstrap-server localhost:9092`).

### Serialization Errors

- Ensure JSON matches the Location class structure.
- Confirm proper Kafka deserialization setup.

### Maven Issues


mvn clean install -U


### Java Version Issues


java -version


Ensure Java 17 is set in JAVA_HOME.
