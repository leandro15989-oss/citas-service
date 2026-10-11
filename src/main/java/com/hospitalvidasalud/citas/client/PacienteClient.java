package com.hospitalvidasalud.citas.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class PacienteClient {

    private final WebClient webClient;

    public PacienteClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public Mono<Boolean> existePaciente(Long pacienteId) {
        return webClient.get()
                .uri("/api/pacientes/{id}", pacienteId)
                .exchangeToMono(response -> {
                    int codigo = response.statusCode().value();

                    if (codigo == 200) {
                        return response.releaseBody()
                                .thenReturn(true);
                    }

                    if (codigo == 404) {
                        return response.releaseBody()
                                .thenReturn(false);
                    }

                    return response.createException()
                            .flatMap(Mono::error);
                });
    }
}