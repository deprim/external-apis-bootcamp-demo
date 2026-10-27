package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BookWebClientImpl implements BookWebClient {

    private WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder,
                             ExternalServiceProperties properties) {
        this.webClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }


    @Override
    public Mono<BookDto> getBookAsync(Long id) {

        Mono<BookApiResponse> response = webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .bodyToMono(BookApiResponse.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.error(new ClientException("Failed to fetch book " + ex.getStatusCode(), ex)))
                .onErrorResume(WebClientRequestException.class, ex ->
                        Mono.error(new ClientException("Book service is unreachable: " + ex.getMessage(), ex)));


        return response.map(BookDto::from);

    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        Flux<BookApiResponse> response = webClient.get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(BookApiResponse.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Flux.error(new ClientException("Failed to fetch books " + ex.getStatusCode(), ex)))
                .onErrorResume(WebClientRequestException.class, ex ->
                        Flux.error(new ClientException("Book service is unreachable: " + ex.getMessage(), ex)));

        return response.map(BookDto::from);
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {

        return Mono.zip(
                getBookAsync(id1),
                getBookAsync(id2)
        ).map(tuple ->
                List.of(tuple.getT1(),
                        tuple.getT2()));

    }
}
