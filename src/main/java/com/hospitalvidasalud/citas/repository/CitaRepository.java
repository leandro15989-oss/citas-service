
package com.hospitalvidasalud.citas.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.hospitalvidasalud.citas.entity.Cita;

public interface CitaRepository extends ReactiveCrudRepository<Cita, Long> {
}
