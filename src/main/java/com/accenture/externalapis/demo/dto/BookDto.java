package com.accenture.externalapis.demo.dto;

import java.math.BigDecimal;

public record BookDto(String title,
                      String author,
                      String genre,
                      BigDecimal price,
                      Integer publishedYear) {

    public static BookDto from(BookApiResponse response) {
        return new BookDto(response.title(),
                response.author(),
                response.genre(),
                response.price(),
                response.publishedYear());
    }

}