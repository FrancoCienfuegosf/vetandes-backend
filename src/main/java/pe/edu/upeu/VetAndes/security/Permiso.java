package pe.edu.upeu.VetAndes.security;

/*
 * Permisos de la matriz 3.2. Cada uno se convierte en una "authority" de
 * Spring Security con el mismo nombre y se exige con hasAuthority(...).
 * Siempre se verifica el PERMISO, nunca el nombre del rol.
 */
public enum Permiso {
    PRESTACION_LEER,
    PRESTACION_ESCRIBIR,
    MASCOTA_LEER,
    MASCOTA_ESCRIBIR,
    ATENCION_LEER,
    ATENCION_REGISTRAR,
    ATENCION_ANULAR,
    REPORTE_VER
}
