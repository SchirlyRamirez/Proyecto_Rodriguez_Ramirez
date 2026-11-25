package com.ejemplo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** RN-2 · Total en pesos enteros, fila 0 (precio 8.000). */
class PedidoTest {

    @Test
    void totalSinDescuento() {
        assertThat(Pedido.calcularTotal(9, 8000, 0)).isEqualTo(72000);
    }

    @Test
    void totalConDescuento() {
        assertThat(Pedido.calcularTotal(10, 8000, 5)).isEqualTo(76000);
    }

    @Test
    void pedidoConfirmadoCalculaSuTotal() {
        Pedido p = Pedido.confirmado("C-002", 1L, 10, 8000, 5);
        assertThat(p.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
        assertThat(p.getTotal()).isEqualTo(76000);
    }

    @Test
    void pedidoRechazadoQuedaEnCero() {
        Pedido p = Pedido.rechazado("C-001", 1L, 42);
        assertThat(p.getEstado()).isEqualTo(EstadoPedido.RECHAZADO);
        assertThat(p.getTotal()).isZero();
    }
}
