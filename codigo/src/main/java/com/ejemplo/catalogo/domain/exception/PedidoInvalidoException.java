package com.ejemplo.catalogo.domain.exception;

/**
 * RN-5 · Error de validación: cantidad <= 0 o producto inexistente.
 * No es un pedido RECHAZADO: no se registra y no consume id.
 */
public class PedidoInvalidoException extends RuntimeException {
    public PedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
