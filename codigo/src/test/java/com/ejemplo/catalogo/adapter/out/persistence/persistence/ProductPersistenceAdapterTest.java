package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.catalogo.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    void findByIdMapsEntityToDomainModel() {
        ProductJpaEntity entity = new ProductJpaEntity("Mouse",
            "Inalámbrico", new BigDecimal("24.90"), 40, "electronica");
        entity.setId(1L);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Product> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Mouse");
        assertThat(result.get().getPrice()).isEqualByComparingTo("24.90");
    }

    @Test
    void saveKeepsTheIdSoUpdatesHitTheSameRow() {
        Product product = new Product("Silla", "Lumbar",
            new BigDecimal("219.00"), 5, "hogar");
        product.setId(3L);
        when(jpaRepository.save(any(ProductJpaEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = adapter.save(product);

        assertThat(saved.getId()).isEqualTo(3L);
        assertThat(saved.getStock()).isEqualTo(5);
    }

    @Test
    void findAllMapsEveryEntity() {
        when(jpaRepository.findAll()).thenReturn(List.of(
            new ProductJpaEntity("A", "a", new BigDecimal("1.00"), 1, "hogar"),
            new ProductJpaEntity("B", "b", new BigDecimal("2.00"), 2, "hogar")));

        assertThat(adapter.findAll()).extracting(Product::getName)
            .containsExactly("A", "B");
    }

    @Test
    void findByNameMapsResults() {
        when(jpaRepository.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(
            new ProductJpaEntity("Laptop", "x", new BigDecimal("9.00"), 1, "electronica")));

        assertThat(adapter.findByNameContainingIgnoreCase("lap")).hasSize(1);
    }

    @Test
    void deleteUsesTheId() {
        Product product = new Product("A", "a", new BigDecimal("1.00"), 1, "hogar");
        product.setId(4L);

        adapter.delete(product);

        verify(jpaRepository).deleteById(4L);
    }
}
