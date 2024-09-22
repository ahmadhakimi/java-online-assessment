package com.app.bookstore.repository;

import com.app.bookstore.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}
