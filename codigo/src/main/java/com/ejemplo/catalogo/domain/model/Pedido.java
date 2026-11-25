package com.ejemplo.catalogo.domain.model;

/**
 * Pedido de una línea. Calcula su total (RN-2) a partir del porcentaje
 * de descuento que le entregan; no sabe cómo se decide ese porcentaje.
 * Dominio puro: sin Spring ni JPA.
 */
public class Pedido {

    private Long id;
    private final String clienteId;
    private final Long productoId;
    private final int cantidad;
    private final EstadoPedido estado;
    private final long total;

    public Pedido(Long id, String clienteId, Long productoId, int cantidad,
                  EstadoPedido estado, long total) {
        this.id = id;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = estado;
        this.total = total;
    }

    /** RN-2: total = cantidad × precio × (100 − descuento) / 100, en pesos enteros. */
    public static long calcularTotal(int cantidad, long precioUnitario, int porcentajeDescuento) {
        return cantidad * precioUnitario * (100 - porcentajeDescuento) / 100;
    }

    public static Pedido confirmado(String clienteId, Long productoId, int cantidad,
                                    long precioUnitario, int porcentajeDescuento) {
        long total = calcularTotal(cantidad, precioUnitario, porcentajeDescuento);
        return new Pedido(null, clienteId, productoId, cantidad, EstadoPedido.CONFIRMADO, total);
    }

    /** RN-3: un pedido rechazado por stock queda con total 0 y no consume stock. */
    public static Pedido rechazado(String clienteId, Long productoId, int cantidad) {
        return new Pedido(null, clienteId, productoId, cantidad, EstadoPedido.RECHAZADO, 0L);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public Long getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public EstadoPedido getEstado() { return estado; }
    public long getTotal() { return total; }
}
