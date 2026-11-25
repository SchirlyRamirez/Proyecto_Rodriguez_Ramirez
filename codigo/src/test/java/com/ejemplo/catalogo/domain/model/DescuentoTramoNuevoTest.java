package com.ejemplo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Hoja 2 (Q1) · Tramo nuevo: desde 3u = 30 el descuento es d + 5 = 10 %. */
class DescuentoTramoNuevoTest {

    private final DescuentoPorVolumen descuento = new DescuentoPorVolumen();

    @Test
    void desdeTresVecesElUmbralElDescuentoEsDiez() {
        assertThat(descuento.porcentajePara(30)).isEqualTo(10);
        assertThat(descuento.porcentajePara(41)).isEqualTo(10);
    }

    @Test
    void justoDebajoDelTramoNuevoSigueEnCinco() {
        assertThat(descuento.porcentajePara(29)).isEqualTo(5);
    }

    @Test
    void totalesDeLaMuestraDeLaHoja2() {
        assertThat(Pedido.calcularTotal(30, 8000, descuento.porcentajePara(30))).isEqualTo(216000);
        assertThat(Pedido.calcularTotal(29, 8000, descuento.porcentajePara(29))).isEqualTo(220400);
        assertThat(Pedido.calcularTotal(1, 8000, descuento.porcentajePara(1))).isEqualTo(8000);
    }
}
