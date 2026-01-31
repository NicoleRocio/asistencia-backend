
package com.colegio.asistencia.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Esto nos ahorra escribir getters y setters a mano
@Entity
@Table(name = "estudiantes") // Así se llamara la tabla en tu MySQL
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(unique = true, nullable = false, length = 20)
    private String dni;

    @Column(name = "grado", nullable = false)
    private String grado;

    @Column(name = "seccion", nullable = false, length = 10)
    private String seccion;

    // Este es el campo clave para el Carnet (QR)
    @Column(name = "codigo_estudiante", unique = true)
    private String codigoEstudiante;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        // Logica de negocio: Si no hay codigo de carnet, usamos el DNI por defecto
        if (this.codigoEstudiante == null) {
            this.codigoEstudiante = this.dni;
        }
    }
}