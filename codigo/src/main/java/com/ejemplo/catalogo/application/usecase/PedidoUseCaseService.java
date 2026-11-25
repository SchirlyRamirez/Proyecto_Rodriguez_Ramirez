package com.ejemplo.catalogo.application.usecase;

import com.ejemplo.catalogo.domain.exception.PedidoInvalidoException;
import com.ejemplo.catalogo.domain.model.DescuentoPorVolumen;
import com.ejemplo.catalogo.domain.model.EstadoPedido;
import com.ejemplo.catalogo.domain.model.Pedido;
import com.ejemplo.catalogo.domain.model.Product;
import com.ejemplo.catalogo.domain.port.in.PedidoService;
import com.ejemplo.catalogo.domain.port.out.PedidoRepository;
import com.ejemplo.catalogo.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso que registra el pedido: valida (RN-5), controla el stock (RN-3)
 * y pide el porcentaje a DescuentoPorVolumen (RN-1). No conoce los tramos.
 */
@Service
@Transactional
public class PedidoUseCaseService implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;
    private final DescuentoPorVolumen descuento = new DescuentoPorVolumen();

    public PedidoUseCaseService(PedidoRepository pedidoRepository,
                                ProductRepository productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Pedido registrar(String clienteId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new PedidoInvalidoException("La cantidad debe ser mayor que cero: " + cantidad);
        }
        Product producto = productRepository.findById(productoId)
            .orElseThrow(() -> new PedidoInvalidoException("No existe el producto " + productoId));

        if (cantidad > producto.getStock()) {
            return pedidoRepository.save(Pedido.rechazado(clienteId, productoId, cantidad));
        }

        int porcentaje = descuento.porcentajePara(cantidad);
        long precio = producto.getPrice().longValue();
        Pedido pedido = Pedido.confirmado(clienteId, productoId, cantidad, precio, porcentaje);

        producto.setStock(producto.getStock() - cantidad);
        productRepository.save(producto);
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoInvalidoException("No existe el pedido " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public long totalPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
            .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
            .mapToLong(Pedido::getTotal)
            .sum();
    }
}
