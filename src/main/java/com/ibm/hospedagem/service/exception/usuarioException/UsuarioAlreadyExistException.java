package com.ibm.hospedagem.service.exception.usuarioException;

import com.ibm.hospedagem.service.exception.ProblemDetail;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
public class UsuarioAlreadyExistException extends RuntimeException {
    private final ProblemDetail problemDetail;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String formattedDateTime = LocalDateTime.now().format(formatter);

    public UsuarioAlreadyExistException(String message) {
        super(message);
        this.problemDetail = new ProblemDetail(
                "http://localhost:8080/usuario/errors/already-exist",
                "Conflict",
                409,
                message,
                formattedDateTime
        );
    }

}