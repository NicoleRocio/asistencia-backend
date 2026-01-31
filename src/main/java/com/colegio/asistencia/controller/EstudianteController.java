package com.colegio.asistencia.controller;

import com.colegio.asistencia.entity.Estudiante;
import com.colegio.asistencia.service.EstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes") // Esta es la raiz de la URL
@CrossOrigin(origins = "*") // IMPORTANTE: Permite que React se conecte despues sin errores
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    // GET http://localhost:8080/api/estudiantes
    @GetMapping
    public List<Estudiante> listar() {
        return estudianteService.obtenerTodos();
    }

    // POST http://localhost:8080/api/estudiantes
    @PostMapping
    public Estudiante crear(@RequestBody Estudiante estudiante) {
        return estudianteService.guardar(estudiante);
    }
}