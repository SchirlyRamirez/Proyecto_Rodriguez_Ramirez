# ADR-0002 · Dónde se absorbió el tramo nuevo de descuento

Variante: Q1 / fila 0

- Fecha: 2026-11-25 14:35
- Estado: aceptada (ADR-0001 se mantiene)

## Contexto

La Hoja 2 (Q1) agregó un tramo de descuento. Desde 3u = 30 cuadernos el descuento es d + 5 = 10 %. De 10 a 29 sigue en 5 %, y por debajo de 10 no hay descuento. La línea 4 de la muestra de la Hoja 1 (41 cuadernos) pasa de 311600 a 295200, como anuncia la Hoja 2. Había que decidir en qué clase poner el tramo dentro de la arquitectura hexagonal elegida en el ADR-0001.

## Alternativas consideradas

- **Poner el tramo en `PedidoUseCaseService`** (un `if` antes de llamar a `DescuentoPorVolumen`) — descartada porque obliga a modificar la clase que registra el pedido y controla el stock de 60. Eso es justo lo que Q1 pide no tocar, y además repartiría la regla de descuento en dos clases.
- **Poner el tramo en `Pedido.calcularTotal`** — descartada porque mezcla la decisión del porcentaje con la fórmula del total (RN-2). Habría que modificar la clase que calcula el total y `PedidoTest`, una prueba de la Hoja 1.
- **Crear una interfaz `ReglaDescuento` con una implementación por tramo** — descartada por sobreabstracción. Son dos tramos de una sola regla, y una clase con dos condiciones es más barata de leer y de cambiar que un motor de reglas.

## Decisión

El tramo se agregó dentro de `domain/model/DescuentoPorVolumen.java`, como una condición previa: si cantidad ≥ 3 × UMBRAL, devuelve PORCENTAJE + 5. Sus pruebas quedaron en una clase nueva, `DescuentoTramoNuevoTest`, y la muestra en `MuestraHoja2`.

## Consecuencias

**Se gana:** el cambio quedó en una sola clase del dominio. No se modificaron:
- `Pedido`, que calcula el total;
- `PedidoUseCaseService`, que registra el pedido;
- el adaptador de persistencia;
- ninguna prueba de la Hoja 1.

`git diff --name-status hoja-1 final` muestra 1 archivo modificado y 2 creados.

**Se pierde:** lo que el ADR-0001 dijo que se perdería sí ocurrió. Agregar los pedidos en la Hoja 1 costó 10 archivos de producción, más que los 9 del mínimo predicho:
- dominio: `Pedido`, `EstadoPedido`, `DescuentoPorVolumen`, `PedidoInvalidoException`;
- puertos: `PedidoService`, `PedidoRepository`;
- caso de uso: `PedidoUseCaseService`;
- persistencia: `PedidoJpaEntity`, `PedidoJpaRepository`, `PedidoPersistenceAdapter`.

Además, los seis campos del pedido se copian a mano dos veces, en `toDomain` y en `toEntity`. Ese costo se pagó en la Hoja 1, y la Hoja 2 no lo recuperó en archivos: en capas el tramo también habría sido un cambio pequeño, solo que dentro de la clase que registra el pedido.

**Veredicto sobre la predicción del ADR-0001:** acertó. Predijo 1 archivo modificado (`DescuentoPorVolumen`), 2 creados (`MuestraHoja2`, `DescuentoTramoNuevoTest`) y ningún cambio en `Pedido`, `PedidoUseCaseService` ni en las pruebas de la Hoja 1, y eso es lo que midió el diff.
