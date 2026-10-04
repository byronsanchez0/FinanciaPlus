# FinanciaPlus

Prototipo de onboarding y originación para una Cuenta Digital + Tarjeta de
Débito. Incluye un backend REST en Java/Spring Boot y una aplicación web
responsive en React.

## Alcance implementado

- Registro básico y persistencia de solicitudes en PostgreSQL.
- Guardado de borradores para continuar el proceso más tarde.
- Consulta AML por documento o nombre (API propia, Supuesto 1).
- Consulta de datos generales y financieros con API key (Supuesto 2).
- Autocompletado para clientes existentes.
- Captura de documento, selfie, prueba de vida y biometría simuladas.
- Evaluación de AML y score mínimo de 7.0.
- Geolocalización con `ipapi.co` durante la originación (Supuesto 3).
- Resultado de aprobación o rechazo visible para el usuario.

## Arquitectura funcional y técnica

```text
React (Vite)
   |
   | HTTP/JSON
   v
Spring Boot REST
   |-- Solicitudes y evaluación
   |-- API AML simulada
   |-- API de clientes simulada + X-API-Key
   |-- Cliente HTTP -> ipapi.co
   v
PostgreSQL + Flyway
```

El frontend separa la comunicación HTTP en `frontend/src/api.js` y mantiene el
flujo de pantalla en componentes React sencillos. En desarrollo, Vite redirige
`/api` hacia Spring Boot, por lo que no se necesita habilitar CORS.

El backend sigue una separación por dominio: controladores REST, servicios con
reglas de negocio, repositorios JPA, DTOs y manejo centralizado de errores. Las
migraciones de Flyway mantienen el esquema versionado.

### Seguridad y sesiones

El backend es stateless. La API de clientes usa el encabezado `X-API-Key`, cuya
clave se obtiene de `CUSTOMER_API_KEY`. La comparación se realiza en tiempo
constante. La clave incluida en el frontend es solo para la demostración local;
en producción no se debe distribuir un secreto en el navegador. La alternativa
recomendada es que el frontend llame a un endpoint de onboarding autenticado y
que el backend consuma internamente la API protegida.

El identificador del borrador se guarda en `localStorage` para poder retomar la
solicitud desde el mismo dispositivo. En producción se usaría una sesión de
usuario autenticada, expiración, HTTPS, rate limiting y gestión de secretos.

### Integraciones y puntos críticos

- AML y score son mocks deterministas para que el flujo sea demostrable.
- OCR, documento, selfie y prueba de vida son simulaciones de interfaz.
- La interfaz recuerda al usuario evitar reflejos, bordes cortados y mala luz.
- Biometría se simula con 92%, por encima del mínimo de 80% de la prueba.
- `ipapi.co` se consulta desde el backend. Una falla se registra, pero no bloquea
  la originación, tal como solicita la regla de negocio.
- Para un sistema real se agregarían detección de documentos duplicados,
  antivirus, almacenamiento cifrado, firma digital y un proveedor biométrico.

### Eventos y logs esenciales

En una versión productiva se registrarían eventos estructurados para creación y
actualización de borradores, resultado AML, consulta de score, cambios de estado,
intentos no autorizados y resultado de geolocalización. Los logs no deben incluir
documentos completos, imágenes, API keys ni otros datos sensibles. Cada evento
debe incluir un identificador de correlación y el ID de la solicitud.

## Ejecución local

Requisitos: Docker, Java 17 y Node.js 20 o superior.

1. Crea el archivo de variables:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Inicia PostgreSQL desde la raíz:

   ```powershell
   docker compose up -d
   ```

3. Inicia el backend:

   ```powershell
   Set-Location backend
   .\mvnw.cmd spring-boot:run
   ```

4. En otra terminal, inicia el frontend:

   ```powershell
   Set-Location frontend
   Copy-Item .env.example .env
   npm install
   npm run dev
   ```

5. Abre `http://localhost:5173`.

Vite enviará las llamadas `/api` a `http://localhost:8080`. Para otro entorno,
configura `VITE_API_URL` antes de compilar el frontend.

## Datos de demostración

| Documento | Resultado esperado |
| --- | --- |
| `1234-ALTO` | Cliente existente, score 8.50, proceso completado |
| `1234-BAJO` | Cliente existente, score 6.40, rechazo por score |
| `1234-AML1` | Rechazo por lista AML |

## Endpoints principales

| Método | Endpoint | Uso |
| --- | --- | --- |
| `GET` | `/api/aml/by-document/{document}` | Consulta AML |
| `GET` | `/api/aml/by-name?name=` | Consulta AML por nombre |
| `GET` | `/api/customers/{document}/general` | Datos generales protegidos |
| `GET` | `/api/customers/{document}/financial` | Score protegido |
| `POST` | `/api/applications` | Crear borrador |
| `GET` | `/api/applications/{id}` | Recuperar borrador |
| `PATCH` | `/api/applications/{id}` | Actualizar borrador |
| `POST` | `/api/applications/{id}/evaluate` | Ejecutar AML y score |
| `POST` | `/api/applications/{id}/originate` | Geolocalizar y completar |
