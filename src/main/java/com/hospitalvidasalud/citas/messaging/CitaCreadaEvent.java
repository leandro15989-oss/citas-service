
package com.hospitalvidasalud.citas.messaging;

import java.time.LocalDateTime;

public record CitaCreadaEvent(
        Long id,
        Long pacienteId,
        LocalDateTime fechaHora,
        String estado
) {
}
