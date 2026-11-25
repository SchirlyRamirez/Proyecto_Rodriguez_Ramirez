package com.ejemplo.catalogo.adapter.out.persistence.config;

import com.ejemplo.catalogo.adapter.out.persistence.persistence.ProductJpaEntity;
import com.ejemplo.catalogo.adapter.out.persistence.persistence.ProductJpaRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Fila 0: al arrancar existe un solo producto, el Cuaderno
 * (productoId 1, precio 8.000, stock 60). Corre antes que las muestras.
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final ProductJpaRepository repository;

    public DataSeeder(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new ProductJpaEntity("Cuaderno", "Producto de la fila 0",
            new BigDecimal("8000"), 60, "papeleria"));
    }
}
