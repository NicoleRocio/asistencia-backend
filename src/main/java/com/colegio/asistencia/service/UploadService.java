package com.colegio.asistencia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {

    // Ruta a la carpeta que acabas de crear
    private final Path rootLocation = Paths.get("C:/sist_escolar/uploads");

    public String guardarImagen(MultipartFile file) {
        try {
            // 1. Crear carpeta si no existe (seguridad extra)
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // 2. Generar nombre único (Ej: "a1b2-juan.jpg")
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 3. Guardar en disco
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));

            // 4. Retornar el nombre para guardarlo en la BD
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen: " + e.getMessage());
        }
    }
}