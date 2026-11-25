package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import com.ejemplo.catalogo.domain.model.Pedido;
import com.ejemplo.catalogo.domain.port.out.PedidoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Cumple el puerto de salida PedidoRepository con JPA (H2 en memoria). */
@Component
@Transactional(readOnly = true)
public class PedidoPersistenceAdapter implements PedidoRepository {

    private final PedidoJpaRepository repository;

    public PedidoPersistenceAdapter(PedidoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Pedido save(Pedido pedido) {
        return toDomain(repository.save(toEntity(pedido)));
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return repository.findById(id).map(PedidoPersistenceAdapter::toDomain);
    }

    @Override
    public List<Pedido> findByClienteId(String clienteId) {
        return repository.findByClienteId(clienteId).stream()
            .map(PedidoPersistenceAdapter::toDomain)
            .toList();
    }

    private static Pedido toDomain(PedidoJpaEntity e) {
        return new Pedido(e.getId(), e.getClienteId(), e.getProductoId(),
            e.getCantidad(), e.getEstado(), e.getTotal());
    }

    private static PedidoJpaEntity toEntity(Pedido p) {
        PedidoJpaEntity e = new PedidoJpaEntity(p.getClienteId(), p.getProductoId(),
            p.getCantidad(), p.getEstado(), p.getTotal());
        e.setId(p.getId());
        return e;
    }
}
