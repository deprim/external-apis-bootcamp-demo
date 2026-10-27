package com.accenture.externalapis.demo.dto;

import java.math.BigDecimal;

public record BookApiResponse(
        Long id,
        String title,
        String author,
        String genre,
        BigDecimal price,
        String isbn,
        Integer publishedYear
) {

}
