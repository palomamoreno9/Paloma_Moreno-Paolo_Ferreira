package com.example.mpaz_api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findBySolicitanteEmail(String solicitanteEmail);
    boolean existsByEspacioNombreAndFechaReservaAndHorarioAndEstadoNot(
            String espacioNombre, String fechaReserva, String horario, String estado
    );
}