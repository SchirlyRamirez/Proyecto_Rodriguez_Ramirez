package com.ejemplo.catalogo.adapter.in.muestra;

import com.ejemplo.catalogo.domain.model.Pedido;
import com.ejemplo.catalogo.domain.port.in.PedidoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada de consola: recorre la arquitectura por el puerto
 * de entrada PedidoService. Fila 0: u = 10, stock = 60.
 */
@Component
@Profile("muestra1")
@Order(10)
public class MuestraHoja1 implements CommandLineRunner {

    private static final long CUADERNO = 1L;
    private final PedidoService pedidos;

    public MuestraHoja1(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public void run(String... args) {
        imprimir(pedidos.registrar("C-001", CUADERNO, 9));   // u - 1
        imprimir(pedidos.registrar("C-002", CUADERNO, 10));  // u
        imprimir(pedidos.registrar("C-001", CUADERNO, 42));  // stock - 2u + 2
        imprimir(pedidos.registrar("C-003", CUADERNO, 41));  // stock - 2u + 1
    }

    private static void imprimir(Pedido p) {
        System.out.println("PEDIDO " + p.getId() + " " + p.getEstado() + " total=" + p.getTotal());
    }
}
