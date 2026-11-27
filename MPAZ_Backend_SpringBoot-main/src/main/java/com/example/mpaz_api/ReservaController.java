package com.example.mpaz_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    @GetMapping("/usuario/{email}")
    public List<Reserva> obtenerPorUsuario(@PathVariable String email) {
        return reservaRepository.findBySolicitanteEmail(email);
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Reserva nuevaReserva) {
        boolean duplicada = reservaRepository
                .existsByEspacioNombreAndFechaReservaAndHorarioAndEstadoNot(
                        nuevaReserva.getEspacioNombre(),
                        nuevaReserva.getFechaReserva(),
                        nuevaReserva.getHorario(),
                        "Rechazada"
                );

        if (duplicada) {
            return ResponseEntity.badRequest()
                    .body("El espacio ya tiene una reserva en esa fecha y horario.");
        }

        if (nuevaReserva.getEstado() == null || nuevaReserva.getEstado().isBlank()) {
            nuevaReserva.setEstado("Pendiente");
        }
        return ResponseEntity.ok(reservaRepository.save(nuevaReserva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizarReserva(@PathVariable Long id, @RequestBody Reserva datos) {
        Optional<Reserva> existente = reservaRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Reserva reserva = existente.get();
        reserva.setEstado(datos.getEstado());
        reserva.setMotivoRechazo(datos.getMotivoRechazo());
        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
