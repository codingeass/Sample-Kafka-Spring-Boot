# Sample-Kafka-Spring-Boot
Sample Program Using Kafka Spring Boot

# Framework and Language Used:
Java 21
Spring Boot 3.3.3
Docker


# Run and Test above sample kafka program
1. Download kafka docker image using command (https://hub.docker.com/r/apache/kafka)

`docker pull apache/kafka`

2. Start kafka broker:

`docker run -d --name broker apache/kafka:latest`

3. Run Spring Boot Application directly from any IDE or from cmd by running jar file
4. Run below command to publish message on the topic created above:
   
 `./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic order.delivery`  

5. We can pass sample message like

     `{"orderName":"pokemon-card", "quantity":1}`
6. Above value will be printed in logs of spring boot application
