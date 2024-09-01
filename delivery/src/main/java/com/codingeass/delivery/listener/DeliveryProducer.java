package com.codingeass.delivery.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.codingeass.delivery.dto.OrderDelivery;
import com.codingeass.delivery.dto.OrderStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryProducer {

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;
	
	public void sendMessageToProducer(OrderDelivery orderDelivery) {
		OrderStatus orderStatus = OrderStatus.builder().orderName(orderDelivery.getOrderName())
				.quantity(orderDelivery.getQuantity())
				.status("INPROGRESS")
				.build();
		kafkaTemplate.send("order.delivery.status",orderStatus);
	}
}
