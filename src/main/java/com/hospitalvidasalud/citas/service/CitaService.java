
package com.hospitalvidasalud.citas.service;

import org.springframework.stereotype.Service;

import com.hospitalvidasalud.citas.client.PacienteClient;
import com.hospitalvidasalud.citas.entity.Cita;
import com.hospitalvidasalud.citas.messaging.CitaCreadaEvent;
import com.hospitalvidasalud.citas.messaging.CitaPublisher;
import com.hospitalvidasalud.citas.repository.CitaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteClient pacienteClient;
    private final CitaPublisher citaPublisher;

    public CitaService(
            CitaRepository citaRepository,
            PacienteClient pacienteClient,
            CitaPublisher citaPublisher) {
        this.citaRepository = citaRepository;
        this.pacienteClient = pacienteClient;
        this.citaPublisher = citaPublisher;
    }

    // Listar todas las citas
    public Flux<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    // Buscar una cita por ID
    public Mono<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    // Registrar una cita y publicar un evento en RabbitMQ
    public Mono<Cita> registrarCita(Cita cita) {

        if (cita.getPacienteId() == null) {
            return Mono.error(new IllegalArgumentException(
                    "El ID del paciente es obligatorio"));
        }

        if (cita.getFechaHora() == null) {
            return Mono.error(new IllegalArgumentException(
                    "La fecha y hora son obligatorias"));
        }

        if (cita.getMotivo() == null
                || cita.getMotivo().isBlank()) {
            return Mono.error(new IllegalArgumentException(
                    "El motivo de la cita es obligatorio"));
        }

        return pacienteClient.existePaciente(cita.getPacienteId())
                .flatMap(existe -> {
                    if (!existe) {
                        return Mono.error(new IllegalArgumentException(
                                "No existe el paciente indicado"));
                    }

                    cita.setId(null);
                    cita.setEstado("PROGRAMADA");

                    return citaRepository.save(cita);
                })
                .flatMap(citaGuardada ->
                    Mono.fromRunnable(() -> {
                        CitaCreadaEvent evento = new CitaCreadaEvent(
                                citaGuardada.getId(),
                                citaGuardada.getPacienteId(),
                                citaGuardada.getFechaHora(),
                                citaGuardada.getEstado()
                        );

                        citaPublisher.publicarCitaCreada(evento);
                    }).thenReturn(citaGuardada)
                );
    }

    // Cambiar el estado de una cita
    public Mono<Cita> cambiarEstado(
            Long id, String nuevoEstado) {

        if (nuevoEstado == null
                || nuevoEstado.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(
                    "El estado es obligatorio"));
        }

        String estado = nuevoEstado.trim().toUpperCase();

        if (!estado.equals("PROGRAMADA")
                && !estado.equals("CONFIRMADA")
                && !estado.equals("CANCELADA")
                && !estado.equals("ATENDIDA")) {
            return Mono.error(new IllegalArgumentException(
                    "Estado no válido"));
        }

        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException(
                                "Cita no encontrada")))
                .flatMap(cita -> {
                    cita.setEstado(estado);
                    return citaRepository.save(cita);
                });
    }

    // Eliminar una cita
    public Mono<Void> eliminarCita(Long id) {
        return citaRepository.deleteById(id);
    }
}
