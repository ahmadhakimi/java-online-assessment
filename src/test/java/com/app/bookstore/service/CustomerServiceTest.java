package com.app.bookstore.service;

import com.app.bookstore.dto.CustomerDTO;
import com.app.bookstore.entity.CustomerEntity;
import com.app.bookstore.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private KafkaProducerCustomerService kafkaProducer;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerDTO = new CustomerDTO(1L, "First", "Last", "email@example.com", "office@example.com", "1234567890", Arrays.asList("rem", "ram"), null, null);
    }

    @Test
    @DisplayName("Test 1: Save Customer Test")
    @Order(1)
    void createCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomer_id(1L);
        customer.setFirst_name("First");  // Ensure first_name is set
        customer.setLast_name("Last");    // Set other necessary fields

        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        Mono<CustomerDTO> result = customerService.createCustomer(customerDTO);

        assertEquals(customerDTO.first_name(), result.block().first_name());
        verify(kafkaProducer).sendCreateCustomerMessage(customerDTO);
    }

    @Test
    @DisplayName("Test 2: Get all customers list test")
    @Order(2)
    void getAllCustomers() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomer_id(1L);
        customer.setFirst_name("First");
        customer.setLast_name("Last");

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        Flux<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.count().block());
    }

    @Test
    @DisplayName("Test 3: View customer by ID Test")
    @Order(3)
    void getCustomerById() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomer_id(1L);
        customer.setFirst_name("First");
        customer.setLast_name("Last");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Mono<CustomerDTO> result = customerService.getCustomerById(1L);

        assertEquals(1L, result.block().customer_id());
    }

    @Test
    @DisplayName("Test 4: Update customer Test")
    @Order(4)
    void updateCustomerById() {
        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setCustomer_id(1L);
        existingCustomer.setFirst_name("Old First");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(existingCustomer);

        Mono<CustomerDTO> result = customerService.updateCustomerById(1L, customerDTO);

        assertEquals("First", result.block().first_name());
        verify(kafkaProducer).sendUpdateCustomerMessage(any());
    }

    @Test
    @DisplayName("Test 5: Delete customer Test")
    @Order(5)
    void deleteCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomer_id(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Mono<Void> result = customerService.deleteCustomer(1L);

        result.block(); // To trigger the Mono
        verify(customerRepository).deleteById(1L);
        verify(kafkaProducer).sendDeleteCustomerMessage(any());
    }
}
