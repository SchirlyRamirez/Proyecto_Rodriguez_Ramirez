package com.ejemplo.catalogo.domain.port.out;

import com.ejemplo.catalogo.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que la aplicación necesita para guardar pedidos. */
public interface PedidoRepository {

    /** Guarda el pedido y lo devuelve con su id consecutivo asignado. */
    Pedido save(Pedido pedido);

    Optional<Pedido> findById(Long id);

    List<Pedido> findByClienteId(String clienteId);
}
