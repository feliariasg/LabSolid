import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PruebasPagoServicios {

    private PagoServiciosService crearServicio(RepositorioFalso repo, NotificadorFalso notificador) {
        CalculadoraComision calculadora = new CalculadoraComision();
        calculadora.registrar(PagoServiciosService.TIPO, new ComisionPagoServicios());

        return new PagoServiciosService(repo, notificador, calculadora,
                (origen, destino, monto) -> { });
    }

    @Test
    void pagoDescuentaMontoMasComisionGuardaEImprimeLaReferencia() {
        RepositorioFalso repo = new RepositorioFalso();
        NotificadorFalso notificador = new NotificadorFalso();
        PagoServiciosService servicio = crearServicio(repo, notificador);

        CuentaAhorros cuenta = new CuentaAhorros("A", "Ana", 500_000);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(salida));
            servicio.pagar(cuenta, "FAC-2026-001", 184_300);
        } finally {
            System.setOut(original);
        }

        Assertions.assertEquals(314_200, cuenta.getSaldo());
        Assertions.assertEquals(1, repo.guardadas.size());
        Assertions.assertEquals("A->FAC-2026-001:184300.0/1500.0", repo.guardadas.get(0));
        Assertions.assertTrue(salida.toString().contains("Destino: FAC-2026-001"));
        Assertions.assertEquals(1, notificador.mensajesEnviados.size());
    }

    @Test
    void pagoSinSaldoSuficienteNoGuardaNiNotifica() {
        RepositorioFalso repo = new RepositorioFalso();
        NotificadorFalso notificador = new NotificadorFalso();
        PagoServiciosService servicio = crearServicio(repo, notificador);

        CuentaAhorros cuenta = new CuentaAhorros("A", "Ana", 185_000);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> servicio.pagar(cuenta, "FAC-2026-001", 184_300)
        );

        Assertions.assertEquals(185_000, cuenta.getSaldo());
        Assertions.assertTrue(repo.guardadas.isEmpty());
        Assertions.assertTrue(notificador.mensajesEnviados.isEmpty());
    }
}
