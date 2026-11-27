# 🧩 MPAZ Reserva — Servidor Backend (Spring Boot)

Servidor desarrollado en **Java con Spring Boot** que funciona como el cerebro de los datos de la app **MPAZ Reserva**. Recibe las solicitudes desde los celulares, revisa que los horarios no choquen y guarda toda la información en la nube.

El servidor está desplegado en **Render** (conectado directamente a la rama `main` mediante Docker), lo que permite que la aplicación Android funcione desde cualquier celular con Internet sin depender de un computador encendido ni de cables USB.

---

## 👥 Equipo

* **Paloma Moreno** — [@palomamoreno9](https://github.com/palomamoreno9)
* **Paolo Ferreira** — [@DustedChara](https://github.com/palomamoreno9) *(Repo colaborador: [@DustedChara](https://github.com/DustedChara))*

**Repositorios:**
* **App Móvil (Android):** [Paloma_Moreno-Paolo_Ferreira](https://github.com/palomamoreno9/Paloma_Moreno-Paolo_Ferreira)
* **Servidor Backend (Spring Boot):** [MPAZ_Backend_SpringBoot](https://github.com/palomamoreno9/MPAZ_Backend_SpringBoot)

---

## ⚙️ ¿Qué hace este servidor?

* **Guarda y organiza las reservas:** Permite crear, ver, actualizar y eliminar reservas en tiempo real desde la aplicación móvil.
* **Evita choques de horario:** Antes de guardar una reserva nueva, revisa automáticamente que no exista otra reserva activa en el mismo espacio, fecha y bloque horario.
* **Base de datos integrada (H2):** Guarda los datos de forma ordenada sin necesidad de configurar servidores de base de datos externos.
* **Siempre disponible en la nube (Render):** Genera una dirección pública `https://...` a la que se conecta la app Android (usando Retrofit) para que lo que crea un docente le aparezca al instante a Inspectoría en otro teléfono.
* **Conexión abierta (CORS):** Configurado para aceptar sin problemas las peticiones que llegan desde la app móvil.

---

## 🔗 Rutas disponibles (Endpoints)

Todas las rutas funcionan sobre la URL pública generada por **Render** (o en `http://localhost:8080` si se prueba en un computador):

| Acción | Ruta | ¿Qué recibe? | ¿Qué hace? |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reservas` | Nada | Devuelve la lista completa de reservas. |
| `GET` | `/api/reservas/usuario/{email}` | Correo en la ruta | Devuelve solo las reservas de ese usuario. |
| `POST` | `/api/reservas` | Datos de la reserva (JSON) | Crea
