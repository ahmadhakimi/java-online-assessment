package com.app.bookstore.service;

import com.app.bookstore.dto.ProductDTO;
import com.app.bookstore.entity.ProductsEntity;
import com.app.bookstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaProducerProductService kafkaProducer;

    @InjectMocks
    private ProductService productService;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDTO = new ProductDTO("1", "Book Title", BigDecimal.valueOf(10.00), 5, null, null);
    }

    @Test
    @DisplayName("Test 1: Save product Test")
    @Order(1)
    @Rollback(value = false)
    void testCreateProduct() {
        ProductsEntity product = new ProductsEntity();
        product.setBook_id("1");
        product.setBook_title("Book Title");
        product.setBook_price(BigDecimal.valueOf(10.00));
        product.setBook_quantity(5);

        when(productRepository.save(any())).thenReturn(product);

        Mono<ProductDTO> result = productService.createNewProduct(productDTO);

        assertEquals(productDTO.book_title(), result.block().book_title());
        verify(kafkaProducer).sendCreateProductMessage(productDTO);
    }

    @Test
    @DisplayName("Test 2: Get all products Test")
    @Order(2)
    void getAllProducts() {
        ProductsEntity product = new ProductsEntity();
        product.setBook_id("1");
        product.setBook_title("Book Title");
        product.setBook_price(BigDecimal.valueOf(10.00));
        product.setBook_quantity(5);

        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        Flux<ProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.count().block());
    }

    @Test
    @DisplayName("Test 3: view product Test")
    @Order(3)
    void getProductById() {
        ProductsEntity product = new ProductsEntity();
        product.setBook_id("1");
        product.setBook_title("Book Title");
        product.setBook_price(BigDecimal.valueOf(10.00));
        product.setBook_quantity(5);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Mono<ProductDTO> result = productService.getProductById("1");

        assertEquals("Book Title", result.block().book_title());
    }

    @Test
    @DisplayName("Test 4: update product Test")
    @Order(4)
    @Rollback(value = false)
    void updateProductById() {
        ProductsEntity existingProduct = new ProductsEntity();
        existingProduct.setBook_id("1");
        existingProduct.setBook_title("Old Title");
        existingProduct.setBook_price(BigDecimal.valueOf(10.00));
        existingProduct.setBook_quantity(5);

        when(productRepository.findById("1")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenReturn(existingProduct);

        Mono<ProductDTO> result = productService.updateProductById("1", productDTO);

        assertEquals("Book Title", result.block().book_title());
        verify(kafkaProducer).sendUpdateProductMessage(any());
    }

    @Test
    @DisplayName("Test 5: delete product Test")
    @Order(5)
    @Rollback(value = false)
    void deleteProduct() {
        ProductsEntity product = new ProductsEntity();
        product.setBook_id("1");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Mono<Void> result = productService.deleteProduct("1");

        result.block(); // To trigger the Mono
        verify(productRepository).deleteById("1");
        verify(kafkaProducer).sendDeleteProductMessage(any());
    }
}
