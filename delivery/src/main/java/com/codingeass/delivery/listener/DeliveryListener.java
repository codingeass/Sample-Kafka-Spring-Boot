package com.codingeass.delivery.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.codingeass.delivery.dto.OrderDelivery;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeliveryListener {

	@KafkaListener(
			id="deliveryClient",
			topics = "order.delivery",
			groupId="dispatch.order.deliveryGroupA")
	public void deliveryListener(OrderDelivery orderDelivery) {
		log.info("Message Received : {}", orderDelivery);
	}
}
