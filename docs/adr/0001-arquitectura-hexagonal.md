# ADR-0001 · Arquitectura hexagonal para la tienda

Variante: Q1 / fila 0

- Fecha: 2026-11-25 13:35
- Estado: aceptada

## Contexto

Hay que construir el mínimo de una tienda con un solo producto, el Cuaderno (productoId 1, precio 8.000, stock 60). Sus pedidos tienen descuento por volumen: 5 % desde 10 unidades. El atributo prioritario es Q1, la modificabilidad de las reglas de descuento: el equipo comercial cambia esas reglas con frecuencia. La Hoja 2 ya anuncia el próximo cambio. Desde 30 unidades (3u) el descuento pasa a 10 % (d + 5), y entre 10 y 29 unidades sigue en 5 %. La pregunta es en qué arquitectura ese cambio cuesta menos y no obliga a tocar lo que no es descuento: el registro del pedido, el control del stock de 60 unidades, la persistencia y la muestra de la Hoja 1.

## Alternativas consideradas

- **Capas** — descartada porque el descuento quedaría dentro de `OrderService`, la misma clase que valida el stock de 60 unidades, maneja la transacción y guarda el pedido. Agregar el tramo de 30 unidades obligaría a modificar la clase que registra el pedido, que es lo que Q1 pide no tocar. La guía hexagonal dice que capas alcanza «si tu próximo cambio es agregar un campo» o en un «CRUD directo». Nuestro próximo cambio no es un campo: es una regla de negocio que cambia seguido, la primera fila de «Conviene hexagonal».
- **Monolito modular** — descartada porque sus «cambios localizados» aíslan a nivel de módulo (`pedidos` frente a `catalogo`), no a nivel de regla. El descuento seguiría junto al registro del pedido dentro de `pedidos`. Además, con un solo producto, la frontera entre `catalogo` y `pedidos` protege algo que el tramo de 30 unidades nunca cruza. Pagaríamos el costo de «decidir dónde cortar» sin que Q1 lo aproveche. El punto de partida también exige adaptar el pedido rechazado por stock, que hoy lanza un 409 y no se guarda.
- **Eventos** — descartada porque su ventaja, sumar consumidores sin tocar al productor, responde a Q2 (extensibilidad), no a Q1. El descuento se calcula de forma síncrona al registrar el pedido, así que el tramo de 30 unidades caería igual dentro del módulo `orders`. A cambio sumaría consistencia eventual a una muestra de cuatro pedidos. La línea 4 (41 unidades) depende de que la línea 3 (42 unidades, rechazada) no haya consumido stock, y eso habría que esperarlo de forma asíncrona. También obliga a adaptar el id de texto, el cliente por correo y la validación síncrona de RN-5.

## Decisión

Usamos la arquitectura hexagonal, partiendo de `Arquitectura hexagonal/catalogo`. La regla de descuento vive en una clase del dominio sin Spring ni JPA, `DescuentoPorVolumen`, separada de la clase que calcula el total (`Pedido`) y de la que registra el pedido (`PedidoUseCaseService`). La persistencia es H2 en memoria, en lugar del PostgreSQL de `compose.yaml`, para que cada arranque sea limpio sin Docker.

Regla de dependencias que verificamos con grep:
1. Nada bajo `domain` importa `org.springframework` ni `jakarta`.
2. Nada bajo `application` importa `com.ejemplo.catalogo.adapter`.

## Consecuencias

**Se gana:** el porcentaje de descuento queda en una sola clase del dominio que se prueba sin levantar Spring ni la base de datos. Un cambio de tramo no debería tocar el caso de uso, el adaptador de persistencia ni la clase que calcula el total.

**Se pierde:** agregar los pedidos en la Hoja 1 cuesta más archivos que en capas. Esperamos crear al menos 9 archivos de producción para los pedidos:
- dominio: `Pedido`, `EstadoPedido`, `DescuentoPorVolumen`;
- puertos: `PedidoService` (entrada) y `PedidoRepository` (salida);
- caso de uso: `PedidoUseCaseService`;
- persistencia: `PedidoJpaEntity`, `PedidoJpaRepository`, `PedidoPersistenceAdapter`.

En capas bastarían unos 5 (entidad, repositorio, servicio, DTO, enum). Además mantenemos dos modelos, `Pedido` y `PedidoJpaEntity`, con un mapeo a mano: si se olvida un campo (por ejemplo `total` o `estado`), el compilador no avisa. Esto se puede comprobar contando los archivos creados antes de la etiqueta `hoja-1`.

**Predicción del costo de la Hoja 2:**
- **Archivos modificados:** 1, `domain/model/DescuentoPorVolumen.java` (se agrega el tramo «cantidad ≥ 3u → d + 5»).
- **Archivos creados:** 2. Son `MuestraHoja2.java`, la muestra de la Hoja 2 (30 → 216000, 29 → 220400, 1 → 8000), y `DescuentoTramoNuevoTest.java`, con las pruebas de 29 y 30 unidades.
- **Total en `git diff --name-status hoja-1 final`:** 3 archivos.
- **¿La clase que calcula el total (`Pedido`)?** No se modifica.
- **¿La clase que registra el pedido (`PedidoUseCaseService`)?** No se modifica.
- **¿Las pruebas de la Hoja 1?** No se modifica ninguna (`DescuentoPorVolumenTest`, `PedidoTest`, `PedidoUseCaseServiceTest`). Sus casos usan cantidades por debajo de 30 o comprueban el estado y no el total.
