package com.colegio.asistencia.repository;

import com.colegio.asistencia.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;     // <--- IMPORTANTE: Necesario para devolver listas de alumnos
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar por DNI (Para evitar duplicados)
    Optional<Estudiante> findByDni(String dni);

    // Buscar por Codigo de Carnet (Para el ingreso)
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);

    // NUEVO: Buscar todo un salón completo (Para la impresión masiva)
    // Spring Boot leerá "ByGradoAndSeccion" y creará el SQL automático:
    // "SELECT * FROM estudiantes WHERE grado = ? AND seccion = ?"
    List<Estudiante> findByGradoAndSeccion(String grado, String seccion);
}