# Revisión cruzada: R6, pago de servicios públicos

Revisaron: Juan Camilo Vergara y Jorge Andrés Hernández

## Lista de revisión

| Lista de revisión | Sí | No |
|---|:---:|:---:|
| Entendimos qué hace cada clase leyendo solo su nombre y sus métodos públicos. | X | |
| Pudimos reutilizar piezas existentes sin copiar y pegar código. | X | |
| Implementamos el requerimiento sin modificar la lógica de clases existentes. | X | |
| No encontramos métodos vacíos ni que lancen "no aplica". | X | |
| No encontramos `if`/`switch` por tipo que tuvimos que extender. | X | |
| Las pruebas existentes siguieron pasando después de nuestro cambio. | X | |
| No encontramos abstracciones innecesarias (interfaces que no aportan). | X | |

Dos aclaraciones. En la primera fila, la única clase que nos hizo dudar fue `TipoTransferencia`. En la sexta, las pruebas pasan pero no con `mvn test` (lo explicamos abajo).

## Lo mejor del diseño

Casi todo lo que necesitábamos ya estaba y se pudo usar sin tocarlo. Lo que más ayudó fue que el repositorio, el comprobante, la auditoría y el antifraude reciben el destino como texto. Así pudimos mandar la referencia de la factura donde normalmente va la cuenta destino.

`Notificador` también fue fácil de usar porque solo recibe un destinatario y un mensaje. Escribimos nuestro mensaje de pago y salió por SMS y PUSH sin hacer nada más.

Lo del CDT nos gustó. Como `pagar` pide una `CuentaRetirable` y el `CDT` no lo es, pasar un CDT ni siquiera compila. No tuvimos que escribir ninguna validación.

La comisión de $1.500 fue una clase nueva (`ComisionPagoServicios`) registrada en `CalculadoraComision`, tal como dice su README. El único archivo de ustedes que modificamos fue `Main.java`, y solo para agregar líneas.

## Lo que nos costó entender o extender

- Al correr `mvn test` sale `BUILD SUCCESS` pero no ejecuta ninguna prueba. Tuvimos que usar `mvn test "-Dtest=Pruebas*"`. Creemos que Maven no las encuentra porque las clases se llaman `Pruebas...`. Revisen si en GitHub Actions les pasa lo mismo.
- `ValidadorTransferencia`, `ComprobanteImpresor` y `AuditoriaLogger` no tienen interfaz y `TransaccionService` los crea con `new`. Nos tocó hacer lo mismo en nuestro servicio. Por eso, para probar el comprobante tuvimos que capturar lo que se imprime en consola.
- El orden de los pasos (guardar, comprobante, notificar, auditoría, antifraude) quedó escrito en `TransaccionService` y otra vez en `PagoServiciosService`. No copiamos lógica, pero si después se agrega un paso hay que ponerlo en los dos.
- El nombre `TipoTransferencia` confunde un poco, porque un pago no es una transferencia y aun así su comisión tiene que implementar esa interfaz.
- En `Main`, el `PostgresRepositorio` y el `AntifraudeConsola` se crean dentro de la llamada al constructor. Para no editar esas líneas creamos otros dos para nuestro servicio.
