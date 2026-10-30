# VetAndes API (vetandes-backend)

Backend REST de la clínica veterinaria VetAndes: Spring Boot 4, Oracle, Flyway, JWT con permisos por rol.
Examen Parcial II · Lenguaje de Programación II · Versión B.

## Requisitos

- JDK 21
- Oracle (XE 21c o Free 23ai) con un **esquema nuevo y vacío** para VetAndes
- Maven (se incluye el wrapper `mvnw`)

### Crear el esquema en Oracle (una sola vez, como SYSTEM)

```sql
ALTER SESSION SET CONTAINER = XEPDB1;   -- o FREEPDB1 en Oracle 23ai Free
CREATE USER vetandes IDENTIFIED BY vetandes QUOTA UNLIMITED ON USERS;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO vetandes;
```

No reutilices el esquema de otro proyecto: Flyway crea aquí las tablas `usuarios`, `roles` y `usuario_roles`, que podrían chocar con tablas existentes.

## Ejecutar (un solo comando)

```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

Al arrancar, Flyway aplica `src/main/resources/db/migration/V1__esquema_y_semilla.sql` (tablas y datos semilla).
La conexión por defecto está en `application-dev.yaml` (`localhost:1522/xepdb1`, usuario `vetandes`).
Si tu Oracle es distinto, cámbiala ahí o usa variables de entorno:

```bash
DB_URL=jdbc:oracle:thin:@//localhost:1521/FREEPDB1 DB_USERNAME=vetandes DB_PASSWORD=vetandes ./mvnw spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html (botón **Authorize**: pega el token sin la palabra `Bearer`)
- Postman: importa `postman/VetAndes.postman_collection.json` y ejecuta las carpetas en orden.
- Pruebas automáticas sin Oracle: `./mvnw test` (perfil `test`, H2 en modo Oracle con la misma migración).

## Usuarios de prueba

| Usuario     | Contraseña | Rol           | Estado   |
|-------------|------------|---------------|----------|
| admin       | Vet2026*   | ADMIN         | Activo   |
| recepcion   | Vet2026*   | RECEPCIONISTA | Activo   |
| veterinario | Vet2026*   | VETERINARIO   | Activo   |
| inactivo    | Vet2026*   | RECEPCIONISTA | Inactivo |

Las contraseñas se guardan como hash BCrypt en la tabla `usuarios`.

## Datos semilla

- 5 propietarios y 8 mascotas (perros y gatos; varios propietarios con dos mascotas).
- 9 prestaciones `PRS-001` a `PRS-009`: `PRS-002` y `PRS-005` solo PERRO, `PRS-003` y `PRS-008` solo GATO, `PRS-009` inactiva.
- 4 atenciones: `AT-000001` a `AT-000003` REGISTRADAS (`AT-000003` con 3 prestaciones y 10 % de descuento) y `AT-000004` ANULADA.
- Las fechas de las atenciones son relativas al momento de la migración. `AT-000001` siempre tiene más de 48 h, así que sirve para probar el 409 de RN-05. Para demostrar una anulación correcta, registra antes una atención nueva.

## Matriz de permisos (sección 3.2)

| Permiso              | ADMIN | RECEPCIONISTA | VETERINARIO |
|----------------------|:-----:|:-------------:|:-----------:|
| PRESTACION_LEER      | Sí    | Sí            | Sí          |
| PRESTACION_ESCRIBIR  | Sí    | No            | Sí          |
| MASCOTA_LEER         | Sí    | Sí            | Sí          |
| MASCOTA_ESCRIBIR     | Sí    | Sí            | No          |
| ATENCION_LEER        | Sí    | Sí            | Sí          |
| ATENCION_REGISTRAR   | Sí    | Sí            | No          |
| ATENCION_ANULAR      | Sí    | No            | Sí          |
| REPORTE_VER          | Sí    | No            | Sí          |

Definida en `security/PermisosPorRol`. Se exige en `config/SecurityConfig` con `hasAuthority(PERMISO)`, y el reporte además con `@PreAuthorize`.

## Contrato del API (base `/api/v1`)

| Verbo                | Ruta                                              | Permiso                                | Códigos                         |
|----------------------|---------------------------------------------------|----------------------------------------|---------------------------------|
| POST                 | /auth/login                                       | Público                                | 200 · 400 · 401                 |
| GET                  | /auth/me                                          | Autenticado                            | 200 · 401                       |
| GET                  | /propietarios                                     | MASCOTA_LEER                           | 200 · 401 · 403                 |
| GET · POST           | /prestaciones?especie&activo                      | PRESTACION_LEER · PRESTACION_ESCRIBIR  | 200 · 201 · 400 · 401 · 403 · 409 |
| GET · PUT · DELETE   | /prestaciones/{id}                                | PRESTACION_LEER · PRESTACION_ESCRIBIR  | 200 · 204 · 400 · 404 · 409     |
| PATCH                | /prestaciones/{id}/estado  `{"activo": false}`    | PRESTACION_ESCRIBIR                    | 200 · 401 · 403 · 404           |
| GET · POST           | /mascotas?propietarioId                           | MASCOTA_LEER · MASCOTA_ESCRIBIR        | 200 · 201 · 400 · 404 · 409     |
| GET · PUT · DELETE   | /mascotas/{id}                                    | MASCOTA_LEER · MASCOTA_ESCRIBIR        | 200 · 204 · 400 · 404 · 409     |
| POST                 | /atenciones                                       | ATENCION_REGISTRAR                     | 201 · 400 · 401 · 403 · 404 · 409 |
| GET                  | /atenciones?mascota&especie&estado&desde&hasta    | ATENCION_LEER                          | 200 · 401 · 403                 |
| GET                  | /atenciones/{id}                                  | ATENCION_LEER                          | 200 · 401 · 403 · 404           |
| PATCH                | /atenciones/{id}/anular  `{"motivo": "..."}`      | ATENCION_ANULAR                        | 200 · 400 · 403 · 404 · 409     |
| GET                  | /reportes/atenciones-por-especie?desde&hasta      | REPORTE_VER                            | 200 · 401 · 403                 |

`?especie=PERRO` en prestaciones devuelve las de PERRO y las de TODAS (para el formulario de atención). Las fechas `desde` y `hasta` van en formato `yyyy-MM-dd`.

### Ejemplos

```json
POST /api/v1/atenciones
{
  "mascotaId": 3,
  "peso": 31.5,
  "diagnostico": "Control y desparasitación",
  "detalles": [
    { "prestacionId": 1, "cantidad": 1 },
    { "prestacionId": 4, "cantidad": 2 },
    { "prestacionId": 6, "cantidad": 1 }
  ]
}
```

Responde 201 con `numero` (AT-000005…), `subtotal` 120.00, `descuento` 12.00 y `total` 108.00. Si el cliente envía `descuento` o `total`, se ignoran.

### Formato de error común

```json
{
  "timestamp": "2026-10-29T09:14:22",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Datos inválidos",
  "ruta": "/api/v1/mascotas",
  "campos": { "fechaNacimiento": "La fecha de nacimiento no puede ser futura" }
}
```

`campos` solo aparece en los errores de validación. Los 401 y 403 usan el mismo formato.

## Reglas de negocio y dónde viven

| Regla | Dónde |
|-------|-------|
| RN-01 código único (409); prestación usada no se borra (409), baja con PATCH | `PrestacionServiceImpl` |
| RN-02 nombre de mascota único por propietario (409), propietario existe (404), mascota con atenciones no se borra (409) | `MascotaServiceImpl` |
| RN-03 1 a 8 detalles, cantidad 1 a 3, peso > 0 (400, Bean Validation); prestación repetida (400); inactiva o de otra especie (409) | `AtencionRequestDTO` y `AtencionServiceImpl.registrar` |
| RN-04 3 o más prestaciones distintas: 10 % de descuento, calculado en el servidor | `AtencionServiceImpl.registrar` |
| RN-05 solo REGISTRADA y dentro de 48 h; motivo de 10 a 200 caracteres | `AnularAtencionRequestDTO` y `AtencionServiceImpl.anular` |

## Estructura

```
src/main/java/pe/edu/upeu/VetAndes/
├── config/      SecurityConfig, JwtService, JwtAuthenticationFilter, CorsConfig, OpenApiConfig
├── security/    Permiso, PermisosPorRol, UsuarioDetailsService,
│                RestAuthenticationEntryPoint (401), RestAccessDeniedHandler (403)
├── entity/      Propietario, Prestacion, Mascota, Atencion, DetalleAtencion, Usuario, Rol
├── repository/  JpaRepository, filtros JPQL y reporte con GROUP BY
├── service/     interfaces + impl/ con las reglas RN y @Transactional
├── controller/  AuthController y controladores REST bajo /api/v1
├── dto/         Request, Response, proyección del reporte y ErrorResponseDTO
├── mapper/      conversiones entidad → DTO
└── exception/   GlobalExceptionHandler y excepciones del dominio
src/main/resources/db/migration/V1__esquema_y_semilla.sql
```
