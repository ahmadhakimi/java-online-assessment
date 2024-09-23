package com.app.bookstore.service;

import com.app.bookstore.dto.ProductDTO;
import com.app.bookstore.entity.ProductsEntity;
import com.app.bookstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;



@Service
@RequiredArgsConstructor
@Slf4j

public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerProductService kafkaProducer;

    public Mono<ProductDTO> createNewProduct (ProductDTO productDTO){
        log.info("Creating product: {}", productDTO);
        ProductsEntity product = ProductsEntity.builder()
                .book_title(productDTO.book_title())
                .book_price(productDTO.book_price())
                .book_quantity(productDTO.book_quantity())
                .build();

        return Mono.just(productRepository.save(product)).flatMap(eachProduct -> {
            kafkaProducer.sendCreateProductMessage(productDTO);
            return mapProductToDTO(eachProduct);
        });
    }

    public Flux<ProductDTO> getAllProducts() {
        log.info("Get list of all products");
        List<ProductsEntity> productList = productRepository.findAll();
        return Flux.fromIterable(productList).flatMap(this::mapProductToDTO);
    }

    public Mono<ProductDTO> getProductById(String id) {
        log.info("Get product by id: {}", id);
        ProductsEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No product available"));

        return Mono.justOrEmpty(product).flatMap(this::mapProductToDTO);
    }

    public Mono<ProductDTO> updateProductById(String id, ProductDTO productDTO) {
        log.info("Update product ID {} details {}", id, productDTO);

        return Mono.justOrEmpty(productRepository.findById(id))
                .flatMap(existingProduct -> {

                    boolean isUpdated = false;

                    if (productDTO.book_title() != null) {
                        existingProduct.setBook_title(productDTO.book_title());
                        isUpdated = true;
                    }
                    if (productDTO.book_price() != null) {
                        existingProduct.setBook_price(productDTO.book_price());
                        isUpdated = true;
                    }
                    if (productDTO.book_quantity() != null) {
                        existingProduct.setBook_quantity(productDTO.book_quantity());
                        isUpdated = true;
                    }

                    if (isUpdated) {
                        existingProduct.setUpdatedAt(Date.from(Instant.now()));
                        ProductsEntity updatedProduct = productRepository.save(existingProduct);

                        kafkaProducer.sendUpdateProductMessage(mapToDTO(updatedProduct));

                        return Mono.just(updatedProduct).flatMap(this::mapProductToDTO);
                    } else {
                        return Mono.just(existingProduct).flatMap(this::mapProductToDTO);
                    }
                });
    }

    public Mono<Void> deleteProduct(String id) {
        log.warn("Deleting product with ID: {}", id);
        return Mono.justOrEmpty(productRepository.findById(id))
                .flatMap(product -> {
                    ProductDTO productDTO = mapToDTO(product);
                    productRepository.deleteById(id);
                    kafkaProducer.sendDeleteProductMessage(productDTO);
                    return Mono.empty();
                });
    }

    private ProductDTO mapToDTO(ProductsEntity product) {
        return new ProductDTO(
                product.getBook_id(),
                product.getBook_title(),
                product.getBook_price(),
                product.getBook_quantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private Mono<ProductDTO> mapProductToDTO(ProductsEntity product) {
        ProductDTO dto = new ProductDTO(
                product.getBook_id(),
                product.getBook_title(),
                product.getBook_price(),
                product.getBook_quantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
        return Mono.just(dto);
    }
}
