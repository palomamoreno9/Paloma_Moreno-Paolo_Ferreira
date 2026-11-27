# 📱 MPAZ Reserva — Aplicación Móvil

Aplicación Android creada en **Kotlin y Jetpack Compose** para que el personal de la **Escuela Escritora Marcela Paz** pueda reservar espacios compartidos de forma ordenada y sin choques de horario. Se conecta a un servidor propio en Spring Boot y a una página externa que entrega los feriados de Chile.

---

## 👥 Equipo

* **Paloma Moreno** — [@palomamoreno9](https://github.com/palomamoreno9)
* **Paolo Ferreira** — [@DustedChara](https://github.com/DustedChara)

**Repositorios:**
* **App Móvil:** [Paloma_Moreno-Paolo_Ferreira](https://github.com/palomamoreno9/Paloma_Moreno-Paolo_Ferreira)
* **Servidor Backend:** [MPAZ_Backend_SpringBoot](https://github.com/palomamoreno9/MPAZ_Backend_SpringBoot)

---

## ⚙️ ¿Qué hace la aplicación?

* **Inicio de sesión y roles:** Permite registrarse e iniciar sesión guardando los datos en el teléfono (Room/SQLite). Separa dos tipos de usuario:
  * **Solicitante** (Docentes, UTP, PIE): Pide espacios y revisa el estado de sus reservas.
  * **Inspectoría** (`inspectoria@mpaz.cl`): Revisa todas las solicitudes para aprobarlas o rechazarlas.
* **Reserva de espacios:** Permite elegir fecha y hora para reservar el Laboratorio de Computación, Laboratorio de Ciencias, Biblioteca, Sala Sensorial o CIEMP.
* **Reglas automáticas:**
  * Solo deja reservar de lunes a viernes.
  * Exige pedir la reserva con al menos 24 horas de anticipación.
  * Impide reservar el mismo lugar en la misma fecha y horario si ya está ocupado.
* **Aprobación y rechazo:** Inspectoría puede aprobar o rechazar pedidos (escribiendo un motivo obligatorio) y el cambio se actualiza en el servidor y en el historial del usuario.
* **Funciones del teléfono:** Permite adjuntar un archivo PDF o imagen de respaldo desde el celular y tiene un botón para llamar directamente a Inspectoría.
* **Feriados de Chile:** Consulta automáticamente una API externa para mostrar los días feriados oficiales en la pantalla principal.
* **Pruebas unitarias:** Incluye pruebas automáticas con JUnit 4 y MockK para revisar que la lógica de reservas funcione bien.

---

## 🔗 Direcciones (Endpoints) que usa la app

### Servidor propio (Spring Boot — `http://localhost:8080/`)
| Acción | Ruta | ¿Para qué sirve? |
| :--- | :--- | :--- |
| `GET` | `/api/reservas` | Trae todas las reservas (para Inspectoría). |
| `GET` | `/api/reservas/usuario/{email}` | Trae solo las reservas de un usuario según su correo. |
| `POST` | `/api/reservas` | Envía y guarda una reserva nueva. |
| `PUT` | `/api/reservas/{id}` | Cambia el estado (`Aprobada` o `Rechazada`) y guarda el motivo. |
| `DELETE` | `/api/reservas/{id}` | Borra una reserva por su número de ID. |

### Servicio externo de Feriados (`https://date.nager.at/`)
| Acción | Ruta | ¿Para qué sirve? |
| :--- | :--- | :--- |
| `GET` | `/api/v3/PublicHolidays/2026/CL` | Trae la lista de feriados oficiales de Chile. |

---

## 🚀 ¿Cómo ejecutar la app?

1. **Encender primero el servidor Backend** (`MPAZ_Backend_SpringBoot`) en el puerto `8080`.
2. **Abrir el proyecto en Android Studio:**
   ```bash
   git clone [https://github.com/palomamoreno9/Paloma_Moreno-Paolo_Ferreira.git](https://github.com/palomamoreno9/Paloma_Moreno-Paolo_Ferreira.git)
