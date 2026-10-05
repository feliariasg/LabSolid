public class PagoServiciosService {
    public static final String TIPO = "PAGO_SERVICIOS";

    private final RepositorioTransacciones repositorio;
    private final Notificador notificador;
    private final CalculadoraComision calculadoraComision;
    private final Antifraude antifraude;
    private final ValidadorTransferencia validador = new ValidadorTransferencia();
    private final ComprobanteImpresor comprobante = new ComprobanteImpresor();
    private final AuditoriaLogger auditoria = new AuditoriaLogger();

    public PagoServiciosService(RepositorioTransacciones repositorio,
                                Notificador notificador,
                                CalculadoraComision calculadoraComision,
                                Antifraude antifraude) {
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.calculadoraComision = calculadoraComision;
        this.antifraude = antifraude;
    }

    public void pagar(CuentaRetirable cuenta, String referenciaFactura, double monto) {
        validador.validar(monto);

        double comision = calculadoraComision.calcular(TIPO, monto);

        cuenta.retirar(monto + comision);

        repositorio.guardarTransaccion(cuenta.getNumero(), referenciaFactura, monto, comision);
        comprobante.imprimir(cuenta.getNumero(), referenciaFactura, monto, comision);
        notificador.enviar(cuenta.getTitular(), "Pagaste $" + monto + " de la factura " + referenciaFactura);
        auditoria.registrar(TIPO, cuenta.getNumero(), referenciaFactura, monto);
        antifraude.reportar(cuenta.getNumero(), referenciaFactura, monto);
    }
}
