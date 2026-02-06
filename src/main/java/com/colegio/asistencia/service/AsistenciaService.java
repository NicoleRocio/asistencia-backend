package com.colegio.asistencia.service;

import com.colegio.asistencia.entity.Asistencia;
import com.colegio.asistencia.entity.Estudiante;
import com.colegio.asistencia.repository.AsistenciaRepository;
import com.colegio.asistencia.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Asistencia registrarAsistencia(Long estudianteId) {
        // 1. Verificar que el estudiante exista
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + estudianteId));

        // 2. Verificar si YA marcó hoy (Para evitar que marquen 2 veces)
        LocalDate hoy = LocalDate.now();
        if (asistenciaRepository.findByEstudianteIdAndFecha(estudianteId, hoy).isPresent()) {
            throw new RuntimeException("El estudiante ya tiene asistencia registrada hoy.");
        }

        // 3. Regla de Negocio: Calcular si es TARDANZA
        LocalTime ahora = LocalTime.now();
        LocalTime horaLimite = LocalTime.of(8, 15); // Digamos que la entrada es hasta las 8:15 AM

        String estado = "PUNTUAL";
        if (ahora.isAfter(horaLimite)) {
            estado = "TARDANZA";
        }

        // 4. Crear y Guardar el registro
        Asistencia asistencia = new Asistencia();
        asistencia.setFecha(hoy);
        asistencia.setHora(ahora);
        asistencia.setEstado(estado);
        asistencia.setEstudiante(estudiante);

        return asistenciaRepository.save(asistencia);
    }

    // Para ver el reporte en la web después
    public List<Asistencia> obtenerTodas() {
        return asistenciaRepository.findAll();
    }
}