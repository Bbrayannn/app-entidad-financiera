package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.TransaccionWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final RealizarConsignacionUseCase consignacionUseCase;
    private final RealizarRetiroUseCase retiroUseCase;
    private final RealizarTransferenciaUseCase transferenciaUseCase;
    private final ConsultarTransaccionUseCase consultarTransaccionUseCase;
    private final TransaccionWebMapper mapper;

    public TransaccionController(RealizarConsignacionUseCase consignacionUseCase,
                                 RealizarRetiroUseCase retiroUseCase,
                                 RealizarTransferenciaUseCase transferenciaUseCase,
                                 ConsultarTransaccionUseCase consultarTransaccionUseCase,
                                 TransaccionWebMapper mapper) {
        this.consignacionUseCase = consignacionUseCase;
        this.retiroUseCase = retiroUseCase;
        this.transferenciaUseCase = transferenciaUseCase;
        this.consultarTransaccionUseCase = consultarTransaccionUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/consignaciones")
    public ResponseEntity<TransaccionResponseDTO> consignar(@Valid @RequestBody ConsignacionRequestDTO request) {
        var t = consignacionUseCase.consignar(request.cuentaId(), request.monto(), request.descripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(t));
    }

    @PostMapping("/retiros")
    public ResponseEntity<TransaccionResponseDTO> retirar(@Valid @RequestBody RetiroRequestDTO request) {
        var t = retiroUseCase.retirar(request.cuentaId(), request.monto(), request.descripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(t));
    }

    @PostMapping("/transferencias")
    public ResponseEntity<TransaccionResponseDTO> transferir(@Valid @RequestBody TransferenciaRequestDTO request) {
        var t = transferenciaUseCase.transferir(request.cuentaOrigenId(), request.cuentaDestinoId(),
                request.monto(), request.descripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(t));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.aResponseDTO(consultarTransaccionUseCase.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> listarPorCuenta(@RequestParam Long cuentaId) {
        var lista = consultarTransaccionUseCase.listarPorCuenta(cuentaId).stream()
                .map(mapper::aResponseDTO).toList();
        return ResponseEntity.ok(lista);
    }
}