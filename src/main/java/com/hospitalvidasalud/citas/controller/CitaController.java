
package com.hospitalvidasalud.citas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hospitalvidasalud.citas.entity.Cita;
import com.hospitalvidasalud.citas.service.CitaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    // Listar todas las citas
    @GetMapping
    public Flux<Cita> listarCitas() {
        return citaService.listarCitas();
    }

    // Buscar una cita por ID
    @GetMapping("/{id}")
    public Mono<Cita> buscarPorId(@PathVariable Long id) {
        return citaService.buscarPorId(id);
    }

    // Registrar una cita
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cita> registrarCita(@RequestBody Cita cita) {
        return citaService.registrarCita(cita);
    }

    // Cambiar el estado de una cita
    @PatchMapping("/{id}/estado")
    public Mono<Cita> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoRequest request) {

        return citaService.cambiarEstado(id, request.getEstado());
    }

    // Eliminar una cita
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminarCita(@PathVariable Long id) {
        return citaService.eliminarCita(id);
    }

    // Datos que recibiremos desde Postman
    public static class EstadoRequest {
        private String estado;

        public EstadoRequest() {
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
