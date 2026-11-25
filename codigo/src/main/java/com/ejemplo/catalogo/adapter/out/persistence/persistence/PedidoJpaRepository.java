package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio de Spring Data sobre PedidoJpaEntity. Interno al adaptador. */
public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Long> {

    List<PedidoJpaEntity> findByClienteId(String clienteId);
}
