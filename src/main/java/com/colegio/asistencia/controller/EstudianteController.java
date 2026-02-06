package com.colegio.asistencia.controller;

import com.colegio.asistencia.entity.Estudiante; // O 'model' según como lo llamaste
import com.colegio.asistencia.service.EstudianteService;
import com.colegio.asistencia.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse; // Asegúrate de tener este import
import java.io.IOException;
import com.colegio.asistencia.service.CarnetService; // Importa tu nuevo servicio

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private UploadService uploadService; // ¡Inyectamos el servicio de fotos!

    @Autowired
    private CarnetService carnetService;

    @GetMapping
    public List<Estudiante> listar() {
        return estudianteService.obtenerTodos();
    }

    // NUEVO ENDPOINT: Descargar Carnet
    @GetMapping("/{id}/carnet")
    public void descargarCarnet(
            @PathVariable Long id,
            @RequestParam(defaultValue = "QR") String tipo, // Recibimos el tipo: QR, BARRA, AMBOS
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/pdf");
        // El nombre del archivo ahora incluye el tipo para que sepas cuál descargaste
        response.setHeader("Content-Disposition", "inline; filename=carnet_" + id + "_" + tipo + ".pdf");

        // Llamamos al servicio con el nuevo parámetro
        carnetService.generarCarnetPDF(id, tipo, response);
    }


    //  ENDPOINT: Descargar TODO el salón
    // URL ejemplo: /api/estudiantes/masivo?grado=5to Secundaria&seccion=A
    @GetMapping("/masivo")
    public void descargarCarnetsMasivos(
            @RequestParam String grado,
            @RequestParam String seccion,
            @RequestParam(defaultValue = "AMBOS") String tipo,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=carnets_" + grado + "_" + seccion + ".pdf");

        carnetService.generarCarnetsPorGrado(grado, seccion, tipo, response);
    }

    // OPCIÓN A: Guardar solo datos (JSON)
    @PostMapping
    public Estudiante crear(@RequestBody Estudiante estudiante) {
        return estudianteService.guardar(estudiante);
    }

    // OPCIÓN B: Guardar Datos + Foto (FormData)
    // Este es el que usaremos desde el Formulario de React
    @PostMapping("/con-foto")
    public Estudiante crearConFoto(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("dni") String dni,
            @RequestParam("grado") String grado,
            @RequestParam("seccion") String seccion,
            @RequestParam(value = "file", required = false) MultipartFile file // La foto es opcional
    ) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombres(nombres);
        estudiante.setApellidos(apellidos);
        estudiante.setDni(dni);
        estudiante.setGrado(grado);
        estudiante.setSeccion(seccion);

        // Si mandaron foto, la guardamos
        if (file != null && !file.isEmpty()) {
            String nombreFoto = uploadService.guardarImagen(file);
            estudiante.setFotoUrl(nombreFoto); // Guardamos "a1b2c3...jpg" en la BD
        }

        return estudianteService.guardar(estudiante);
    }
}