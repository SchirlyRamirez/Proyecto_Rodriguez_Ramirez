package com.ejemplo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** RN-1 · Descuento por volumen, fila 0 (u = 10, d = 5). */
class DescuentoPorVolumenTest {

    private final DescuentoPorVolumen descuento = new DescuentoPorVolumen();

    @Test
    void porDebajoDelUmbralNoHayDescuento() {
        assertThat(descuento.porcentajePara(1)).isZero();
        assertThat(descuento.porcentajePara(9)).isZero();
    }

    @Test
    void elUmbralMismoYaRecibeDescuento() {
        assertThat(descuento.porcentajePara(10)).isEqualTo(5);
    }

    @Test
    void porEncimaDelUmbralSigueElDescuento() {
        assertThat(descuento.porcentajePara(20)).isEqualTo(5);
    }
}
