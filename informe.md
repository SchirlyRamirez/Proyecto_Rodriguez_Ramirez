# Informe · Q1 / fila 0 · Juan Esteban Rodríguez y Schirly Ramirez Lopez

## Medición (`git diff --name-status hoja-1 final`)

| Medida | Resultado |
|---|---|
| Archivos creados | 2: `adapter/in/muestra/MuestraHoja2.java`, `test/.../domain/model/DescuentoTramoNuevoTest.java` |
| Archivos modificados | 1: `domain/model/DescuentoPorVolumen.java` |
| ¿Se modificó el archivo o la clase que calcula el total? | No (`Pedido`) |
| ¿Se modificó el archivo o la clase que registra el pedido? | No (`PedidoUseCaseService`) |
| ¿Se tocó una prueba de la Hoja 1? | No |

## Predicción contra medición

El ADR-0001 predijo un archivo modificado, `DescuentoPorVolumen`, y dos creados, `MuestraHoja2` y `DescuentoTramoNuevoTest`, para un total de tres. La tabla muestra exactamente esos tres archivos, con las mismas letras A y M. También acertó en lo que no se tocaría: ni `Pedido`, que calcula el total, ni `PedidoUseCaseService`, que registra el pedido, ni ninguna prueba de la Hoja 1 aparecen en el diff. El acierto dependió de una decisión de la Hoja 1: separar el porcentaje de descuento de la fórmula del total, y escribir las pruebas de la Hoja 1 con cantidades menores que 30 para que el tramo nuevo no las afectara, aunque la línea 4 de la muestra de la Hoja 1 sí cambió de 311600 a 295200.

## El límite

Si la tienda pidiera que cada pedido confirmado emitiera una factura y avisara al cliente, hexagonal sería peor que eventos. Tendríamos que crear un puerto de salida por reacción y modificar `PedidoUseCaseService` para llamarlos, o sea, reabrir el flujo de registro cada vez. La guía de eventos presenta como ventaja sumar consumidores sin tocar al productor, y su «Cuándo usar eventos y cuándo no» lo recomienda cuando las reacciones a un hecho crecen. La guía hexagonal, en «Desventajas», ya advierte que cada necesidad nueva suma archivos: aquí sumaría puertos y adaptadores sin aislar el registro del pedido. Para el tramo de descuento, en cambio, eventos no habría ayudado, porque el porcentaje se calcula de forma síncrona dentro del mismo registro.

## Lo que la regla no ve

La violación inducida fue agregar `import org.springframework.stereotype.Component;` en `domain/model/Pedido.java`. El primer grep la detectó e imprimió la línea con su número, y después la revertimos con `git checkout`. Esa regla no detectaría que `MuestraHoja1`, un adaptador de entrada, importara `PedidoJpaRepository` y guardara pedidos directamente, saltándose el caso de uso y la regla de stock. No lo vería porque los dos grep solo revisan `domain` y `application`, y la regla no dice nada sobre dependencias entre adaptadores. Tampoco detectaría una anotación escrita con el nombre completo, `@org.springframework.stereotype.Component`, porque el grep busca la palabra `import`.
