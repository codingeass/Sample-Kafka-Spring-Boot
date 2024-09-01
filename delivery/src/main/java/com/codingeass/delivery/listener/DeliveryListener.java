package com.codingeass.delivery.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.codingeass.delivery.dto.OrderDelivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryListener {

	@Autowired
	DeliveryProducer deliveryProducer;
	
	@KafkaListener(
			id="deliveryClient",
			topics = "order.delivery",
			groupId="dispatch.order.deliveryGroupA")
	public void deliveryListener(OrderDelivery orderDelivery) {
		log.info("Message Received : {}", orderDelivery);
		deliveryProducer.sendMessageToProducer(orderDelivery);
	}
}
