package com.ibm.hospedagem.controller.exceptionHandle;

import com.ibm.hospedagem.service.exception.ProblemDetail;
import com.ibm.hospedagem.service.exception.usuarioException.UsuarioAlreadyExistException;
import com.ibm.hospedagem.service.exception.usuarioException.UsuarioBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UsuarioExceptionHandle {

    @ExceptionHandler(UsuarioBadRequestException.class)
    public ResponseEntity<ProblemDetail> handleUsuarioBadRequest(UsuarioBadRequestException badRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestException.getProblemDetail());
    }

    @ExceptionHandler(UsuarioAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleUsuarioAlreadyExist(UsuarioAlreadyExistException AlreadyExistException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AlreadyExistException.getProblemDetail());
    }

}
