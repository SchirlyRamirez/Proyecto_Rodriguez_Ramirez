package com.ejemplo.catalogo.domain.model;

/**
 * RN-1 · Descuento por volumen (fila 0: Cuaderno, u = 10, d = 5 %).
 * Única clase que conoce los tramos de descuento. Dominio puro: sin Spring ni JPA.
 */
public class DescuentoPorVolumen {

    public static final int UMBRAL = 10;     // u
    public static final int PORCENTAJE = 5;  // d

    /** Porcentaje de descuento (0..100) que corresponde a una cantidad. */
    public int porcentajePara(int cantidad) {
        if (cantidad >= UMBRAL) {
            return PORCENTAJE;
        }
        return 0;
    }
}
