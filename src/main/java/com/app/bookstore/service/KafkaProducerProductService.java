package com.app.bookstore.service;

import com.app.bookstore.dto.KafkaMessageCustomer;
import com.app.bookstore.dto.KafkaMessageProduct;
import com.app.bookstore.dto.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaProducerProductService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCreateProductMessage(ProductDTO productDTO) {
        sendMessage("create", productDTO);
    }

    public void sendUpdateProductMessage(ProductDTO productDTO) {
        sendMessage("update", productDTO);
    }

    public void sendDeleteProductMessage(ProductDTO productDTO) {
        sendMessage("delete", productDTO);
    }

    private void sendMessage(String action, ProductDTO productDTO) {
        try {
            // Convert productDTO to a JSON message
            String message = objectMapper.writeValueAsString(new KafkaMessageProduct(action, productDTO));
            // Send the message to the Kafka topic
            kafkaTemplate.send("product-event-id", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message", e);
        }
    }
}
