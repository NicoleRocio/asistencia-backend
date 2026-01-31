package com.colegio.asistencia.repository;

import com.colegio.asistencia.entity.Estudiante; // Importamos la clase que acabas de crear
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar por DNI (Para evitar registrar dos veces al mismo alumno)
    Optional<Estudiante> findByDni(String dni);

    // Buscar por Codigo de Carnet (Para cuando uses la pistola/QR en la puerta)
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);
}