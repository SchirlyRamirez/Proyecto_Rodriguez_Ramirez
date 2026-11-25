package com.ejemplo.catalogo.adapter.in.muestra;

import com.ejemplo.catalogo.domain.model.Pedido;
import com.ejemplo.catalogo.domain.port.in.PedidoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Muestra de la Hoja 2 (Q1, tramo nuevo). Fila 0: u = 10, 3u = 30. */
@Component
@Profile("muestra2")
@Order(10)
public class MuestraHoja2 implements CommandLineRunner {

    private static final long CUADERNO = 1L;
    private final PedidoService pedidos;

    public MuestraHoja2(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public void run(String... args) {
        imprimir(pedidos.registrar("C-001", CUADERNO, 30));  // 3u: tramo nuevo (d + 5)
        imprimir(pedidos.registrar("C-002", CUADERNO, 29));  // 3u - 1: descuento d
        imprimir(pedidos.registrar("C-003", CUADERNO, 1));   // sin descuento
    }

    private static void imprimir(Pedido p) {
        System.out.println("PEDIDO " + p.getId() + " " + p.getEstado() + " total=" + p.getTotal());
    }
}
