package com.colegio.asistencia.controller;

import com.colegio.asistencia.entity.Asistencia;
import com.colegio.asistencia.repository.AsistenciaRepository;
import com.colegio.asistencia.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    // 1. MARCAR ASISTENCIA
    @PostMapping("/{estudianteId}")
    public Asistencia marcar(@PathVariable Long estudianteId) {
        return asistenciaService.registrarAsistencia(estudianteId);
    }

    // ==========================================
    //       REPORTES Y CONSULTAS 📊
    // ==========================================

    // 2. Ver TODAS las asistencias de HOY
    @GetMapping("/hoy")
    public List<Asistencia> obtenerAsistenciasDeHoy() {
        return asistenciaRepository.findByFecha(LocalDate.now());
    }

    // 3. Ver solo las TARDANZAS de HOY
    @GetMapping("/tardanzas")
    public List<Asistencia> obtenerTardanzasDeHoy() {
        return asistenciaRepository.findByFechaAndEstado(LocalDate.now(), "TARDANZA");
    }

    // 4. Ver historial completo de UN ALUMNO (¡Solo dejamos este!)
    @GetMapping("/alumno/{id}")
    public List<Asistencia> historialAlumno(@PathVariable Long id) {
        return asistenciaRepository.findByEstudianteId(id);
    }

    // 5. Buscar por FECHA ESPECÍFICA
    @GetMapping("/fecha")
    public List<Asistencia> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        return asistenciaRepository.findByFecha(dia);
    }

    // 6. Reporte por GRADO con FILTRO (Estado opcional)
    @GetMapping("/grado")
    public List<Asistencia> reportePorGrado(
            @RequestParam String grado,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();

        if (estado != null && !estado.isEmpty() && !estado.equals("TODOS")) {
            return asistenciaRepository.findByFechaAndEstudiante_GradoAndEstado(fechaBusqueda, grado, estado);
        } else {
            return asistenciaRepository.findByFechaAndEstudiante_Grado(fechaBusqueda, grado);
        }
    }

    // 7. Reporte por SECCIÓN con FILTRO
    @GetMapping("/seccion")
    public List<Asistencia> reportePorSeccion(
            @RequestParam String grado,
            @RequestParam String seccion,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();

        if (estado != null && !estado.isEmpty() && !estado.equals("TODOS")) {
            return asistenciaRepository.findByFechaAndEstudiante_GradoAndEstudiante_SeccionAndEstado(
                    fechaBusqueda, grado, seccion, estado);
        } else {
            return asistenciaRepository.findByFechaAndEstudiante_GradoAndEstudiante_Seccion(
                    fechaBusqueda, grado, seccion);
        }
    }
}