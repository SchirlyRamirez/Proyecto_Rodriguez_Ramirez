package com.ejemplo.catalogo.domain.port.in;

import com.ejemplo.catalogo.domain.model.Pedido;

/** Puerto de entrada: lo que la aplicación ofrece sobre pedidos. */
public interface PedidoService {

    /** Registra un pedido (CONFIRMADO o RECHAZADO) o lanza PedidoInvalidoException (RN-5). */
    Pedido registrar(String clienteId, Long productoId, int cantidad);

    /** RN-4: consulta un pedido por su id. */
    Pedido buscarPorId(Long id);

    /** RN-4: suma de los total de los pedidos CONFIRMADO del cliente. */
    long totalPorCliente(String clienteId);
}
