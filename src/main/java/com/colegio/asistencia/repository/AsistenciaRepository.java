package com.colegio.asistencia.repository;

import com.colegio.asistencia.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // 1. Evitar duplicados (Ya lo tienes)
    Optional<Asistencia> findByEstudianteIdAndFecha(Long estudianteId, LocalDate fecha);

    // 2. Reporte General por Fecha (Todos los del colegio)
    List<Asistencia> findByFecha(LocalDate fecha);

    // 3. Reporte de Tardanzas por Fecha
    List<Asistencia> findByFechaAndEstado(LocalDate fecha, String estado);

    // 4. Reporte por ALUMNO (Historial completo)
    List<Asistencia> findByEstudianteId(Long estudianteId);

    // 5. Reporte por GRADO y SECCIÓN (Ej: 3ro B)
    List<Asistencia> findByFechaAndEstudiante_GradoAndEstudiante_Seccion(LocalDate fecha, String grado, String seccion);

    // 6. Reporte solo por GRADO (Ej: Todo 3ro Secundaria)
    List<Asistencia> findByFechaAndEstudiante_Grado(LocalDate fecha, String grado);



    //  7: Buscar por GRADO y ESTADO (Ej: "Dame todas las TARDANZAS de 3ro Secundaria")
    List<Asistencia> findByFechaAndEstudiante_GradoAndEstado(LocalDate fecha, String grado, String estado);

    // 8: Buscar por SECCIÓN y ESTADO (Ej: "Dame los PUNTUALES de 3ro B")
    List<Asistencia> findByFechaAndEstudiante_GradoAndEstudiante_SeccionAndEstado(LocalDate fecha, String grado, String seccion, String estado);



}