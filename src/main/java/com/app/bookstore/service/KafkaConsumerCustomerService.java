package com.app.bookstore.service;

import com.app.bookstore.dto.CustomerDTO;
import com.app.bookstore.dto.KafkaMessageCustomer;
import com.app.bookstore.entity.CustomerEntity;
import com.app.bookstore.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerCustomerService {

    private final ObjectMapper objectMapper;
    private final CustomerRepository customerRepository;

    @KafkaListener(topics = "customer-event-id", groupId = "bookstore-groupID")
    public void receivedMessage (String message) {
        try {
            KafkaMessageCustomer kafkaMessageCustomer = objectMapper.readValue(message, KafkaMessageCustomer.class);
            log.info("Received message---- {}", kafkaMessageCustomer);

            switch (kafkaMessageCustomer.getAction()) {
                case "create":
                    handleCreateCustomer(kafkaMessageCustomer.getData()) ;
                    break;
                case "update":
                    handleUpdateCustomer(kafkaMessageCustomer.getData());
                    break;
                case "delete":
                    handleDeleteCustomer(kafkaMessageCustomer.getData());
                    break;
                default:
                    log.warn("Unknown action {}", kafkaMessageCustomer.getAction());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to process the message: {}", message, e );

        }

    }

    private void handleUpdateCustomer(CustomerDTO customerDTO) {
        customerRepository.findById(customerDTO.customer_id()).ifPresent(customer -> {
            if (customerDTO.first_name() != null) customer.setFirst_name(customerDTO.first_name());
            if (customerDTO.last_name() != null) customer.setLast_name(customerDTO.last_name());
            if (customerDTO.email_office() != null) customer.setEmail_office(customerDTO.email_office());
            if (customerDTO.email_personal() != null) customer.setEmail_personal(customerDTO.email_personal());
            if (customerDTO.family_member() != null) customer.setFamily_member(customerDTO.family_member());
            if (customerDTO.phone_no() != null) customer.setPhone_no(customerDTO.phone_no());
            customerRepository.save(customer);
            log.info("Customer updated: {}", customer);
        });
    }

    private void handleCreateCustomer(CustomerDTO data) {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .first_name(data.first_name())
                .last_name(data.last_name())
                .email_personal(data.email_personal())
                .email_office(data.email_office())
                .phone_no(data.phone_no())
                .family_member(data.family_member())
                .build();

        customerRepository.save(customerEntity);
        log.info("Save customer dto: {}", customerEntity );

    }

    private void handleDeleteCustomer(CustomerDTO customerDTO) {
        customerRepository.deleteById(customerDTO.customer_id());
        log.info("Customer deleted with ID: {}", customerDTO.customer_id());
    }

}
