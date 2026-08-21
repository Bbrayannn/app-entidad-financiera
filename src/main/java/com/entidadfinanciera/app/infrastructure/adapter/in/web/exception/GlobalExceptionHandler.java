package com.entidadfinanciera.app.infrastructure.adapter.in.web.exception;

import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler({
            ClienteNoEncontradoException.class,
            CuentaNoEncontradaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(RuntimeException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 - Conflicto: el estado actual del recurso impide la operación
    @ExceptionHandler({
            ClienteConProductosVinculadosException.class,
            ClienteDuplicadoException.class,
            CuentaNoCanceladaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarConflicto(RuntimeException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400 - Solicitud inválida por regla de negocio
    @ExceptionHandler({
            ClienteMenorDeEdadException.class,
            SaldoInvalidoParaCancelarException.class,
            SaldoNegativoNoPermitidoException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarSolicitudInvalida(RuntimeException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 500 - Error interno inesperado (ej. no se pudo generar número de cuenta único)
    @ExceptionHandler(GeneracionNumeroCuentaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorInterno(GeneracionNumeroCuentaException ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // 400 - Errores de validación de @Valid en los DTOs (ej. @Email, @NotBlank, @Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Fallback - cualquier excepción no contemplada explícitamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarGenerico(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno inesperado: " + ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuesta(HttpStatus status, String mensaje) {
        ErrorResponseDTO error = new ErrorResponseDTO(status.value(), status.getReasonPhrase(), mensaje);
        return ResponseEntity.status(status).body(error);
    }
}