package com.ibm.hospedagem.service.exception.hospedagemException;

import com.ibm.hospedagem.service.exception.ProblemDetail;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
public class HospedagemNotFoundException extends RuntimeException {
    private final ProblemDetail problemDetail;

    public HospedagemNotFoundException(String message) {
        super(message);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        this.problemDetail = new ProblemDetail(
                "http://localhost:8080/reservas/errors/reserva-not-found",
                "Reserva Not Found",
                404,
                message,
                formattedDateTime
        );
    }
}
