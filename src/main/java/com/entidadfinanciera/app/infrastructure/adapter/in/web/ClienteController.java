package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteRequestDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteResponseDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.ClienteWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;
    private final ClienteWebMapper mapper;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                             ActualizarClienteUseCase actualizarClienteUseCase,
                             EliminarClienteUseCase eliminarClienteUseCase,
                             ConsultarClienteUseCase consultarClienteUseCase,
                             ClienteWebMapper mapper) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.eliminarClienteUseCase = eliminarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO request) {
        var creado = crearClienteUseCase.crear(mapper.aDominio(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(creado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        var cliente = consultarClienteUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.aResponseDTO(cliente));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        var clientes = consultarClienteUseCase.listarTodos().stream()
                .map(mapper::aResponseDTO)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody ClienteRequestDTO request) {
        var actualizado = actualizarClienteUseCase.actualizar(id, mapper.aDominio(request));
        return ResponseEntity.ok(mapper.aResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarClienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}