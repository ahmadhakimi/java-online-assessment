package com.app.bookstore.repository;

import com.app.bookstore.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductsEntity, String> {

}
