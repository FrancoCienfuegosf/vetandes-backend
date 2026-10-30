package pe.edu.upeu.VetAndes.exception;

/* 409: los datos son validos, pero una regla de negocio (RN) lo impide */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
