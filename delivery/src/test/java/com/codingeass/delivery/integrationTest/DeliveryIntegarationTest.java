package com.codingeass.delivery.integrationTest;

import org.hamcrest.Matchers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.codingeass.delivery.config.DeliveryConfiguration;
import com.codingeass.delivery.dto.OrderDelivery;
import com.codingeass.delivery.dto.OrderStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = {DeliveryConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
public class DeliveryIntegarationTest {

	@Autowired
	private KafkaTemplate kafkaTemplate;
	
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@Autowired
	private KafkaListenerEndpointRegistry registry;
	
	@Autowired
	private KafkaTestListener kafkaTestListener;
	
	@Configuration
	static class TestConfiguration {
		
		@Bean
		public KafkaTestListener testListener() {
			return new KafkaTestListener();
		}
	}
	
	static class KafkaTestListener {
		volatile int deliveryStatusCounter = 0;
		
		@KafkaListener(
				id="deliveryStatus",
				topics = "order.delivery.status",
				groupId="dispatch.order.deliveryGroupA")
		 void receiveDispatchPreparing(@Payload OrderStatus orderStatus) {
            log.info("Received : " + orderStatus);
            deliveryStatusCounter++;
        }
	}
	
	@BeforeEach
	public void setup() {
		kafkaTestListener.deliveryStatusCounter = 0;
		registry.getListenerContainers().stream().forEach(container -> ContainerTestUtils.waitForAssignment(container,
				embeddedKafkaBroker.getPartitionsPerTopic()));
	}
	
	@Test
	public void testDeliveryFlow() throws InterruptedException, ExecutionException {
		OrderDelivery orderDelivery = OrderDelivery.builder().orderName("Car")
				.quantity(3)
				.build();
		kafkaTemplate.send(MessageBuilder.withPayload(orderDelivery)
				.setHeader(KafkaHeaders.TOPIC, "order.delivery")
				.build()).get();
		Awaitility.await().atMost(6, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(()->(Integer)kafkaTestListener.deliveryStatusCounter, Matchers.<Integer>equalTo(1));

	}
}
