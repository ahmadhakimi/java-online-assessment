package com.app.bookstore.controller;

import com.app.bookstore.dto.ProductDTO;
import com.app.bookstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDTO> createNewProduct( @RequestBody ProductDTO productDTO) {
        log.info("Calling URL createNewProduct");
        return productService.createNewProduct(productDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductDTO> productList(){
        log.info("Calling URL productList");
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductDTO> viewProduct(@PathVariable String id){
        log.info("Calling URL viewProduct");
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductDTO> updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        log.info("Calling URL updateProduct");
        return productService.updateProductById(id, productDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable String id) {
        log.info("Calling URL deleteProduct");
        return productService.deleteProduct(id);
    }
}
