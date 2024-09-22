package com.app.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record ProductDTO(
         String book_id,
         String book_title,
         BigDecimal book_price,
         Integer book_quantity,
         Date createdAt,
         Date updatedAt
    ) {
}
