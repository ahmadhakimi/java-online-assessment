package com.app.bookstore.service;

import com.app.bookstore.dto.CustomerDTO;
import com.app.bookstore.dto.KafkaMessageCustomer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerCustomerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCreateCustomerMessage(CustomerDTO customerDTO) {
        sendMessage("create", customerDTO);
    }

    public void sendUpdateCustomerMessage(CustomerDTO customerDTO) {
        sendMessage("update", customerDTO);
    }

    public void sendDeleteCustomerMessage(CustomerDTO customerDTO) {
        sendMessage("delete", customerDTO);
    }

    private void sendMessage(String action, CustomerDTO dto) {
        KafkaMessageCustomer kafkaMessageCustomer = new KafkaMessageCustomer(action, dto);
        try {
            String message = objectMapper.writeValueAsString(kafkaMessageCustomer);
            kafkaTemplate.send("customer-event-id", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
