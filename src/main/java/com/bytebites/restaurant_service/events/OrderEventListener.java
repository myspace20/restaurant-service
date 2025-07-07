package com.bytebites.restaurant_service.events;


import com.bytebites.restaurant_service.dto.OrderResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OrderEventListener {

    private final Logger logger = Logger.getLogger(OrderEventListener.class.getName());
    @KafkaListener(topics = {"OrderPlacedEvent"})
    public void listen(OrderResponse order) {
        logger.log(Level.INFO, "Received order: " + order);
    }

}
