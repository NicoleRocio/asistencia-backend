package com.colegio.asistencia.service;

import com.colegio.asistencia.entity.Estudiante;
import com.colegio.asistencia.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    // 1. Listar todos (Para verlos en la tabla de asistencia)
    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }

    // 2. Guardar un estudiante nuevo
    public Estudiante guardar(Estudiante estudiante) {
        // Validacion basica: Ver si el DNI ya existe
        if (estudianteRepository.findByDni(estudiante.getDni()).isPresent()) {
            throw new RuntimeException("El estudiante con DNI " + estudiante.getDni() + " ya existe.");
        }
        // Si no tiene codigo de carnet, se genera en la Entidad automaticamente, así que solo guardamos.
        return estudianteRepository.save(estudiante);
    }

    // 3. Buscar por ID (Para generar el carnet individual)
    public Optional<Estudiante> obtenerPorId(Long id) {
        return estudianteRepository.findById(id);
    }
}