# Tienda · Arquitectura hexagonal (Q1 / fila 0)

Integrantes: Juan Esteban Rodríguez y Schirly Ramirez Lopez.
Requisitos: Java 21. No hace falta instalar Maven: `codigo/` trae el Maven Wrapper (`./mvnw` en Git Bash, `.\mvnw.cmd` en PowerShell), que lo descarga la primera vez. Base de datos H2 en memoria: cada arranque es limpio, no hace falta Docker.

## Muestra de la Hoja 1

Se corre sobre la etiqueta `hoja-1` (`git checkout hoja-1`; para volver: `git checkout main`). Sobre `final` la línea 4 da 295200 en lugar de 311600, como anuncia la Hoja 2.

```
cd codigo
./mvnw -q spring-boot:run "-Dspring-boot.run.profiles=muestra1" "-Dspring-boot.run.arguments=--spring.main.web-application-type=none"
```

## Muestra de la Hoja 2

```
cd codigo
./mvnw -q spring-boot:run "-Dspring-boot.run.profiles=muestra2" "-Dspring-boot.run.arguments=--spring.main.web-application-type=none"
```

## Pruebas

```
cd codigo
./mvnw test
```

## Regla de arquitectura (Git Bash, en la carpeta codigo; las dos líneas no deben imprimir nada)

```
grep -rn "import org.springframework\|import jakarta" src/main/java/com/ejemplo/catalogo/domain
grep -rn "import com.ejemplo.catalogo.adapter" src/main/java/com/ejemplo/catalogo/application
```
