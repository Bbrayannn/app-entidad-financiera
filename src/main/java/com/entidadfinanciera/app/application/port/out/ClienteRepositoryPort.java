package com.entidadfinanciera.app.application.port.out;

import com.entidadfinanciera.app.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Page<Cliente> listarTodos(Pageable pageable); // <--- Reemplaza a List<Cliente> listarTodos()
    boolean existePorNumeroIdentificacion(String numeroIdentificacion);
    boolean existePorCorreo(String correo);
    void eliminar(Long id);
    boolean tieneCuentasVinculadas(Long clienteId);
}