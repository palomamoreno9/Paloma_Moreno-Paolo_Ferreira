package com.example.mpaz_api;

import jakarta.persistence.*;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String solicitanteEmail;
    private String espacioNombre;
    private String fechaReserva;
    private String horario;
    private String actividad;
    private String documentoUrl = "";
    private String estado = "Pendiente";
    private String motivoRechazo = "";

    public Reserva() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSolicitanteEmail() { return solicitanteEmail; }
    public void setSolicitanteEmail(String solicitanteEmail) { this.solicitanteEmail = solicitanteEmail; }

    public String getEspacioNombre() { return espacioNombre; }
    public void setEspacioNombre(String espacioNombre) { this.espacioNombre = espacioNombre; }

    public String getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getDocumentoUrl() { return documentoUrl; }
    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}