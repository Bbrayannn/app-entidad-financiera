package com.entidadfinanciera.app.infrastructure.adapter.in.web.exception;

import com.entidadfinanciera.app.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 400 Bad Request ----
    @ExceptionHandler({
            ClienteMenorDeEdadException.class,
            MontoInvalidoException.class,
            TransferenciaEntreMismaCuentaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarBadRequest(RuntimeException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex, request);
    }

    // ---- 404 Not Found ----
    @ExceptionHandler({
            ClienteNoEncontradoException.class,
            CuentaNoEncontradaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarNotFound(RuntimeException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex, request);
    }

    // ---- 409 Conflict ----
    @ExceptionHandler({
            ClienteConProductosVinculadosException.class,
            ClienteDuplicadoException.class,
            SaldoInsuficienteException.class,
            SaldoInvalidoParaCancelarException.class,
            SaldoNegativoNoPermitidoException.class,
            CuentaInactivaException.class,
            TransicionEstadoInvalidaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarConflict(RuntimeException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, ex, request);
    }

    // ---- 400 Bad Request: validaciones de @Valid en DTOs ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        ErrorResponseDTO body = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Error de validación en los datos enviados",
                request.getRequestURI(),
                detalles
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ---- 500 Internal Server Error: fallo técnico esperado ----
    @ExceptionHandler(GeneracionNumeroCuentaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorTecnico(RuntimeException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    // ---- 500 Internal Server Error: fallback genérico ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGenerico(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado. Contacte al administrador.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarJsonInvalido(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "El cuerpo de la petición contiene datos inválidos o mal formados (revise tipos de dato como tipoIdentificacion, tipoCuenta, etc.)",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuesta(HttpStatus status, RuntimeException ex,
                                                                HttpServletRequest request) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}