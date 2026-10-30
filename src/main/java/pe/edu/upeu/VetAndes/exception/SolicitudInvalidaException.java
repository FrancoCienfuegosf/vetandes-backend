package pe.edu.upeu.VetAndes.exception;

/*
 * 400: datos invalidos que no se pueden expresar con Bean Validation
 * (por ejemplo, una prestacion repetida en los detalles, RN-03).
 */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
