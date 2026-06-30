# Cinema Manager

[English](README.md) · [Página del proyecto](https://pierrelaguerre.github.io/cinema-manager-javafx/) · [Descargar](../../releases/latest)

![Interfaz de Cinema Manager](docs/assets/preview.svg)

Cartelera bilingüe en JavaFX, reconstruida a partir de mi examen final de Java de primer curso. La aplicación de consola original demostraba POO, colecciones y ficheros; esta edición de portfolio convierte esa base en una experiencia de escritorio cuidada.

## Funciones destacadas

- Interfaz JavaFX con FXML y CSS
- Español e inglés mediante `ResourceBundle`
- Búsqueda por título o dirección, sin distinguir mayúsculas ni tildes
- Sesiones ordenadas y disponibilidad de aforo
- Reserva de entradas con validación y persistencia local
- Arquitectura por capas y pruebas JUnit 5
- Compilación automatizada con GitHub Actions

## Ejecutar localmente

Necesitas JDK 21 o posterior, pero no hace falta instalar Maven:

```powershell
.\mvnw.cmd clean javafx:run
```

Las reservas se guardan en `~/.cinema-manager/cinema-data.txt`. Borra ese archivo para recuperar los datos iniciales.

El examen intacto permanece en el primer commit y en `Cine_Final/`. Las películas y recursos visuales de la demo son ficticios.

## Licencia

[MIT](LICENSE)
