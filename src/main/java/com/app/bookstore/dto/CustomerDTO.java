package com.app.bookstore.dto;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

public record CustomerDTO(
        Long customer_id,
        String first_name,
        String last_name,
        String email_personal,
        String email_office,
        String phone_no,
        Date createdAt,
        Date updatedAt
) {
}
