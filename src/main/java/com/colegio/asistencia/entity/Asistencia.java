package com.colegio.asistencia.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha; // Ejemplo: 2025-02-03

    @Column(nullable = false)
    private LocalTime hora;  // Ejemplo: 08:05:00

    @Column(nullable = false)
    private String estado;   // PUNTUAL, TARDANZA, FALTA

    // RELACIÓN CLAVE: Esto crea la columna 'estudiante_id' en MySQL automáticamente
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
}
