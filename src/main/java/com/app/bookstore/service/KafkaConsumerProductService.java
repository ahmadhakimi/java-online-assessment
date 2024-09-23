package com.app.bookstore.service;

import com.app.bookstore.dto.KafkaMessageProduct;
import com.app.bookstore.dto.ProductDTO;
import com.app.bookstore.entity.ProductsEntity;
import com.app.bookstore.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerProductService {
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-event-id", groupId = "bookstore-groupID")
    public void receivedMessage(String message) {
        try {
            KafkaMessageProduct kafkaMessage = objectMapper.readValue(message, KafkaMessageProduct.class);
            log.info("Received product message: {}", kafkaMessage);
            switch (kafkaMessage.getAction()) {
                case "create":
                    handleCreateProduct(kafkaMessage.getData());
                    break;
                case "update":
                    handleUpdateProduct(kafkaMessage.getData());
                    break;
                case "delete":
                    handleDeleteProduct(kafkaMessage.getData());
                    break;
                default:
                    log.warn("Unknown action: {}", kafkaMessage.getAction());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCreateProduct(ProductDTO data) {
        ProductsEntity product = ProductsEntity.builder()
                .book_title(data.book_title())
                .book_quantity(data.book_quantity())
                .book_price(data.book_price())
                .build();
        productRepository.save(product);
        log.info("Save product: {}", product );

    }

    private void handleUpdateProduct(ProductDTO data) {
        productRepository.findById(data.book_id()).ifPresent(productsEntity -> {
            if(data.book_title() != null) productsEntity.setBook_title(data.book_title());
            if(data.book_quantity() != null) productsEntity.setBook_quantity(data.book_quantity());
            if(data.book_price()!= null) productsEntity.setBook_price(data.book_price());
            productRepository.save(productsEntity);
            log.info("Update product: {}", productsEntity );
        });

    }

    private void handleDeleteProduct(ProductDTO data) {
        productRepository.deleteById(data.book_id());
        log.info("Delete product: {}", data.book_id());
    }
}
