package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.CambiarEstadoCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.CrearCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.EliminarCuentaUseCase;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.CambiarEstadoRequestDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.CuentaRequestDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.CuentaResponseDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.CuentaWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;
    private final ConsultarCuentaUseCase consultarCuentaUseCase;
    private final EliminarCuentaUseCase eliminarCuentaUseCase;
    private final CuentaWebMapper mapper;

    public CuentaController(CrearCuentaUseCase crearCuentaUseCase,
                            CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase,
                            ConsultarCuentaUseCase consultarCuentaUseCase,
                            EliminarCuentaUseCase eliminarCuentaUseCase,
                            CuentaWebMapper mapper) {
        this.crearCuentaUseCase = crearCuentaUseCase;
        this.cambiarEstadoCuentaUseCase = cambiarEstadoCuentaUseCase;
        this.consultarCuentaUseCase = consultarCuentaUseCase;
        this.eliminarCuentaUseCase = eliminarCuentaUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO request) {
        var creada = crearCuentaUseCase.crear(request.clienteId(), request.tipoCuenta(), request.exentaGmf());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.aResponseDTO(consultarCuentaUseCase.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> listarPorCliente(@RequestParam Long clienteId) {
        var cuentas = consultarCuentaUseCase.listarPorCliente(clienteId).stream()
                .map(mapper::aResponseDTO).toList();
        return ResponseEntity.ok(cuentas);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CuentaResponseDTO> cambiarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody CambiarEstadoRequestDTO request) {
        var actualizada = cambiarEstadoCuentaUseCase.cambiarEstado(id, request.nuevoEstado());
        return ResponseEntity.ok(mapper.aResponseDTO(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarCuentaUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}