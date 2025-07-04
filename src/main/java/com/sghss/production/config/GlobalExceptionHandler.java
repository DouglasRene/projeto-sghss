// src/main/java/com/sghss/production/config/GlobalExceptionHandler.java
package com.sghss.production.config;

import com.sghss.production.dto.error.ErrorResponse;
import com.sghss.production.exception.DataConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.crossstore.ChangeSetPersister; // Para NotFoundException
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento para CPF/usuário duplicado (Exceção customizada)
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponse> handleDataConflictException(DataConflictException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(), // 409
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Tratamento para Not Found (404)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ChangeSetPersister.NotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(), // 404
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "Recurso não encontrado.", // Mensagem genérica, ou 'ex.getMessage()' se a exceção tiver uma
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Tratamento para erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(), // 400
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Erro de validação: " + errorMessage,
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Tratamento para qualquer outra exceção não mapeada (Erro genérico 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.",
            request.getRequestURI()
        );
        // Opcional: logar a exceção completa para depuração
        ex.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}