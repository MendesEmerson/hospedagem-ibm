package com.ibm.hospedagem.controller.exceptionHandle;

import com.ibm.hospedagem.service.exception.ProblemDetail;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HospedagemExceptionHandle {

    @ExceptionHandler(HospedagemBadRequestException.class)
    public ResponseEntity<ProblemDetail> handleHospedagemBadRequest(HospedagemBadRequestException badRequestException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestException.getProblemDetail());
    }

    @ExceptionHandler(HospedagemNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleHospedagemNotFound(HospedagemNotFoundException notFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getProblemDetail());
    }
}
