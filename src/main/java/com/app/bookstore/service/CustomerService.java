package com.app.bookstore.service;

import com.app.bookstore.dto.CustomerDTO;
import com.app.bookstore.entity.CustomerEntity;
import com.app.bookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

//    inject customer jpa repository
    private final CustomerRepository customerRepository;
    private final KafkaProducerCustomerService kafkaProducer;

//    create customer business logic using CustomerDTO
    public Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO) {
        log.info("Creating customer: {}", customerDTO);
        CustomerEntity customer = CustomerEntity.builder()
                .first_name(customerDTO.first_name())
                .last_name(customerDTO.last_name())
                .email_office(customerDTO.email_office())
                .email_personal(customerDTO.email_personal())
                .phone_no(customerDTO.phone_no())
                .family_member(customerDTO.family_member())
                .build();


        return Mono.just(customerRepository.save(customer)).flatMap(customerEntity -> {
            kafkaProducer.sendCreateCustomerMessage(customerDTO);
            return mapToCustomerDTO(customerEntity);
        });
    }


//    get all customers list
    public Flux<CustomerDTO> getAllCustomers() {
        log.info("Get list of all customers");
        List<CustomerEntity> customers = customerRepository.findAll();
        return Flux.fromIterable(customers).flatMap(customer->mapToCustomerDTO(customer));
    }

//    get customer by id
    public Mono<CustomerDTO> getCustomerById(Long id) {
        log.info("Get customer by id: {}", id);

        return Mono.justOrEmpty(customerRepository.findById(id)).flatMap(customer->mapToCustomerDTO(customer));
    }

//    update customer by id
    public Mono<CustomerDTO> updateCustomerById(Long id, CustomerDTO customerDTO) {
        log.info("Update customer ID {} details {}", id, customerDTO);

        return Mono.justOrEmpty(customerRepository.findById(id)).flatMap(customer -> {
            if(customerDTO.first_name() != null) {
                customer.setFirst_name(customerDTO.first_name());
            }
            if(customerDTO.last_name() != null) {
                customer.setLast_name(customerDTO.last_name());
            }
            if(customerDTO.email_office() != null) {
                customer.setEmail_office(customerDTO.email_office());
            }
            if(customerDTO.email_personal() != null) {
                customer.setEmail_personal(customerDTO.email_personal());
            }
            if(customerDTO.phone_no() != null) {
                customer.setPhone_no(customerDTO.phone_no());
            }
            if (customerDTO.family_member() != null) { // Updated
                customer.setFamily_member(customerDTO.family_member());
            }

            CustomerEntity save = customerRepository.save(customer);
            kafkaProducer.sendUpdateCustomerMessage(customerDTO);
            log.info("Update customer: {}", customerDTO.toString());
            return Mono.just(save);
        }).flatMap(savedCustomer -> mapToCustomerDTO(savedCustomer));
    }

//    delete customer by id
public Mono<Void> deleteCustomer(Long id) {
    log.warn("Deleting customer with ID: {}", id);
    return Mono.justOrEmpty(customerRepository.findById(id))
            .flatMap(customer -> {
                CustomerDTO customerDTO = mapToDTO(customer);  
                customerRepository.deleteById(id);
                kafkaProducer.sendDeleteCustomerMessage(customerDTO); 
                return Mono.empty();
            });
}

    private CustomerDTO mapToDTO(CustomerEntity customerEntity) {
        return new CustomerDTO(
                customerEntity.getCustomer_id(),
                customerEntity.getFirst_name(),
                customerEntity.getLast_name(),
                customerEntity.getEmail_personal(),
                customerEntity.getEmail_office(),
                customerEntity.getPhone_no(),
                customerEntity.getFamily_member(),
                customerEntity.getCreatedAt(),
                customerEntity.getUpdatedAt()
        );
    }

    // mapping entity to dto method
    private Mono<CustomerDTO> mapToCustomerDTO(CustomerEntity saveNewCustomer) {
        CustomerDTO dto = new CustomerDTO(
                saveNewCustomer.getCustomer_id(),
                saveNewCustomer.getFirst_name(),
                saveNewCustomer.getLast_name(),
                saveNewCustomer.getEmail_personal(),
                saveNewCustomer.getEmail_office(),
                saveNewCustomer.getPhone_no(),
                saveNewCustomer.getFamily_member(),
                saveNewCustomer.getCreatedAt(),
                saveNewCustomer.getUpdatedAt()
        );

        return Mono.just(dto);
    }
}
