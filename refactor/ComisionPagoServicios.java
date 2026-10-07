public class ComisionPagoServicios implements TipoTransferencia {
    private static final double COMISION_FIJA = 1_500;

    @Override
    public double calcularComision(double monto) {
        return COMISION_FIJA;
    }
}
