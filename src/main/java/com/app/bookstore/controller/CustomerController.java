package com.app.bookstore.controller;

import com.app.bookstore.dto.CustomerDTO;
import com.app.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j

public class CustomerController {

//    inject service
    private final CustomerService customerService;

//    create user endpoint
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerDTO> createNewCustomer (@RequestBody CustomerDTO customerDTO) {
        log.info("calling URL createNewCustomer");
        return customerService.createCustomer(customerDTO);
    }

//    get list of users endpoint

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDTO> getAllCustomers() {
        log.info("calling URL getAllCustomers");
        return customerService.getAllCustomers();
    }

//    view customer endpoint
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CustomerDTO> getCustomerByID(@PathVariable Long id) {
        log.info("calling URL getCustomerByID");
        return customerService.getCustomerById(id);
    }

//    update customer id
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        log.info("Calling URL updateCustomer");
        return customerService.updateCustomerById(id, customerDTO);
    }

//    delete customer endpoint
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Calling URL deleteCustomer");
        return customerService.deleteCustomer(id);
    }

}
