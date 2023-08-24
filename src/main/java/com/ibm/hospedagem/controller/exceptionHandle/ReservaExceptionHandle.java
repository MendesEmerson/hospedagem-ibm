package com.ibm.hospedagem.controller.exceptionHandle;

import com.ibm.hospedagem.service.exception.ProblemDetail;
import com.ibm.hospedagem.service.exception.reservaException.ReservaBadRequestException;
import com.ibm.hospedagem.service.exception.reservaException.ReservaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservaExceptionHandle {

    @ExceptionHandler(ReservaBadRequestException.class)
    public ResponseEntity<ProblemDetail> handleHospedagemBadRequest(ReservaBadRequestException badRequestException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestException.getProblemDetail());
    }

    @ExceptionHandler(ReservaNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleHospedagemNotFound(ReservaNotFoundException notFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getProblemDetail());
    }
}
