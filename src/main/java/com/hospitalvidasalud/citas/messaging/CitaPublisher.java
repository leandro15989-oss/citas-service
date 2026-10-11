package com.hospitalvidasalud.citas.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CitaPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CitaPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCitaCreada(CitaCreadaEvent evento) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.COLA_CITAS,
            evento
        );

        System.out.println(
            "Evento de cita enviado a RabbitMQ: " + evento.id()
        );
    }
}