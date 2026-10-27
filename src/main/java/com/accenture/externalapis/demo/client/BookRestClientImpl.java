package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Arrays;
import java.util.List;


@Component
public class BookRestClientImpl implements BookRestClient {

    private final RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder,
                              ExternalServiceProperties properties) {
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }


    @Override
    public BookDto getBook(Long id) {

        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);

            if (response == null) {
                throw new ClientException("External API returned an empty response.");
            }

            return BookDto.from(response);

        } catch (HttpClientErrorException e) {
            throw new ClientException("Client error: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new ClientException("Server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new ClientException("Connection error: " + e.getMessage(), e);
        }

    }



    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] response = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);


            if (response == null) {
                throw new ClientException("External API returned an empty response.");
            }

            return Arrays.stream(response)
                    .map(BookDto::from)
                    .toList();

        } catch (HttpClientErrorException e) {
            throw new ClientException("Client error: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new ClientException("Server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new ClientException("Connection error: " + e.getMessage(), e);
        }
    }

}
