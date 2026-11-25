package com.ejemplo.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.catalogo.domain.exception.PedidoInvalidoException;
import com.ejemplo.catalogo.domain.model.EstadoPedido;
import com.ejemplo.catalogo.domain.model.Pedido;
import com.ejemplo.catalogo.domain.model.Product;
import com.ejemplo.catalogo.domain.port.out.PedidoRepository;
import com.ejemplo.catalogo.domain.port.out.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RN-3 (stock) y RN-5 (validación), con los puertos de salida simulados. */
@ExtendWith(MockitoExtension.class)
class PedidoUseCaseServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PedidoUseCaseService service;

    private Product cuadernoConStock(int stock) {
        Product p = new Product("Cuaderno", "fila 0", new BigDecimal("8000"), stock, "papeleria");
        p.setId(1L);
        return p;
    }

    @Test
    void pedidoQueSePasaDelStockQuedaRechazadoYNoConsumeStock() {
        Product cuaderno = cuadernoConStock(41);
        when(productRepository.findById(1L)).thenReturn(Optional.of(cuaderno));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido p = service.registrar("C-001", 1L, 42);

        assertThat(p.getEstado()).isEqualTo(EstadoPedido.RECHAZADO);
        assertThat(p.getTotal()).isZero();
        assertThat(cuaderno.getStock()).isEqualTo(41);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void pedidoQueDejaElStockEnCeroSeConfirma() {
        Product cuaderno = cuadernoConStock(41);
        when(productRepository.findById(1L)).thenReturn(Optional.of(cuaderno));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido p = service.registrar("C-003", 1L, 41);

        assertThat(p.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
        assertThat(cuaderno.getStock()).isZero();
    }

    @Test
    void cantidadCeroEsErrorDeValidacionYNoSeRegistra() {
        assertThatThrownBy(() -> service.registrar("C-001", 1L, 0))
            .isInstanceOf(PedidoInvalidoException.class);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void productoInexistenteEsErrorDeValidacionYNoSeRegistra() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar("C-001", 99L, 5))
            .isInstanceOf(PedidoInvalidoException.class);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}
