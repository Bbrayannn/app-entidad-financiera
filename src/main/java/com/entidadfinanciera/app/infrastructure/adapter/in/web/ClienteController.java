package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteRequestDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteResponseDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.ClienteWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes de la entidad financiera")
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

    @Operation(summary = "Crear un nuevo cliente", description = "Registra un cliente validando mayoría de edad y unicidad de identificación/correo.")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO request) {
        var creado = crearClienteUseCase.crear(mapper.aDominio(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponseDTO(creado));
    }

    @Operation(summary = "Obtener cliente por ID", description = "Consulta la información detallada de un cliente registrado por su identificador único.")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        var cliente = consultarClienteUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.aResponseDTO(cliente));
    }

    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista completa de clientes registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        var clientes = consultarClienteUseCase.listarTodos().stream()
                .map(mapper::aResponseDTO)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente por su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody ClienteRequestDTO request) {
        var actualizado = actualizarClienteUseCase.actualizar(id, mapper.aDominio(request));
        return ResponseEntity.ok(mapper.aResponseDTO(actualizado));
    }

    @Operation(summary = "Eliminar cliente", description = "Inactiva o elimina un cliente del sistema siempre que no tenga cuentas vinculadas.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarClienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}