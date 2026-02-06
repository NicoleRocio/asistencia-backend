package com.colegio.asistencia.service;

import com.colegio.asistencia.entity.Estudiante;
import com.colegio.asistencia.repository.EstudianteRepository;
import com.colegio.asistencia.util.GeneradorQR;
import com.lowagie.text.*;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CarnetService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    public void generarCarnetPDF(Long estudianteId, String tipoCodigo, HttpServletResponse response) throws IOException {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // === CAMBIO CLAVE: TAMAÑO CR-80 (Tarjeta de Crédito) ===
        // Ancho: ~153 puntos (5.4cm)
        // Alto:  ~243 puntos (8.6cm)
        Rectangle tamanioCarnet = new Rectangle(153, 243);

        // Márgenes muy pequeños (5 puntos) porque el papel es chico
        Document document = new Document(tamanioCarnet, 10, 10, 10, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // PÁGINA 1: FRENTE
            agregarFrente(document, estudiante);

            document.newPage(); // Salto de página para el reverso

            // PÁGINA 2: REVERSO
            agregarReverso(document, writer, estudiante, tipoCodigo);

            document.close();

        } catch (DocumentException e) {
            throw new IOException("Error al crear el PDF: " + e.getMessage());
        }
    }

    // NUEVO MÉTODO: IMPRESIÓN MASIVA POR GRADO
    public void generarCarnetsPorGrado(String grado, String seccion, String tipoCodigo, HttpServletResponse response) throws IOException {

        // CORRECCIÓN AQUÍ: Especificamos "java.util.List" para que no se confunda con la List del PDF
        java.util.List<Estudiante> estudiantes = estudianteRepository.findByGradoAndSeccion(grado, seccion);

        if (estudiantes.isEmpty()) {
            throw new RuntimeException("No hay alumnos en el grado " + grado + " - " + seccion);
        }

        // Configuración CR-80
        Rectangle tamanioCarnet = new Rectangle(153, 243);
        Document document = new Document(tamanioCarnet, 10, 10, 10, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // EL BUCLE MÁGICO
            for (Estudiante estudiante : estudiantes) {
                // Página 1: Frente
                agregarFrente(document, estudiante);

                document.newPage(); // Salto

                // Página 2: Reverso
                agregarReverso(document, writer, estudiante, tipoCodigo);

                // Salto para el siguiente alumno
                document.newPage();
            }

            document.close();

        } catch (DocumentException e) {
            throw new IOException("Error al crear el PDF masivo: " + e.getMessage());
        }
    }

    private void agregarFrente(Document document, Estudiante estudiante) throws DocumentException, IOException {
        // LOGO / TÍTULO (Letra más pequeña: 10)
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
        Paragraph titulo = new Paragraph("COLEGIO TÉCNICO", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        // 2. FOTO DEL ESTUDIANTE (Modo Tamaño Carnet Vertical)
        if (estudiante.getFotoUrl() != null) {
            try {
                Path rutaFoto = Paths.get("C:/sist_escolar/uploads").resolve(estudiante.getFotoUrl());
                if (Files.exists(rutaFoto)) {
                    Image foto = Image.getInstance(rutaFoto.toAbsolutePath().toString());

                    // === EL CAMBIO CLAVE ===
                    // Antes era (85, 85). Ahora le damos forma rectangular vertical.
                    // Esto asegura que si suben una foto tamaño carnet, encaje perfecta.
                    foto.scaleToFit(80, 110);

                    foto.setSpacingBefore(10); // Un poco más de aire arriba
                    foto.setSpacingAfter(10);
                    foto.setBorder(Rectangle.BOX);
                    foto.setBorderWidth(1f); // Borde un poco más notorio
                    foto.setBorderColor(Color.BLACK);
                    foto.setAlignment(Element.ALIGN_CENTER);
                    document.add(foto);
                }
            } catch (Exception e) {
                document.add(new Paragraph("[Sin Foto]", FontFactory.getFont(FontFactory.HELVETICA, 6, Color.RED)));
            }
        }

        // NOMBRE (Letra 9 y 8)
        Font fuenteNombre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Paragraph nombre = new Paragraph(estudiante.getNombres().toUpperCase(), fuenteNombre);
        nombre.setAlignment(Element.ALIGN_CENTER);
        document.add(nombre);

        Paragraph apellido = new Paragraph(estudiante.getApellidos().toUpperCase(), fuenteNombre);
        apellido.setAlignment(Element.ALIGN_CENTER);
        document.add(apellido);

        // DNI
        Font fuenteDNI = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        Paragraph dni = new Paragraph("DNI: " + estudiante.getDni(), fuenteDNI);
        dni.setAlignment(Element.ALIGN_CENTER);
        dni.setSpacingBefore(3);
        document.add(dni);
    }

    private void agregarReverso(Document document, PdfWriter writer, Estudiante estudiante, String tipoCodigo) throws DocumentException {
        // Título Reverso
        Paragraph info = new Paragraph("INFO ACADÉMICA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8));
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);

        // Grado y Sección
        Font fuenteGrado = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Paragraph grado = new Paragraph(estudiante.getGrado(), fuenteGrado);
        grado.setAlignment(Element.ALIGN_CENTER);
        document.add(grado);

        Paragraph seccion = new Paragraph("Sección: " + estudiante.getSeccion(), fuenteGrado);
        seccion.setAlignment(Element.ALIGN_CENTER);
        document.add(seccion);

        document.add(new Paragraph(" ")); // Pequeño espacio

        // CÓDIGOS (QR / BARRAS)
        PdfContentByte cb = writer.getDirectContent();

        if (tipoCodigo.equalsIgnoreCase("BARRA") || tipoCodigo.equalsIgnoreCase("AMBOS")) {
            try {
                Barcode128 code128 = new Barcode128();
                code128.setCode(estudiante.getDni());
                code128.setCodeType(Barcode128.CODE128);
                code128.setBarHeight(30f); // Barras más bajitas
                code128.setSize(8f); // Texto del número más pequeño

                Image codeImage = code128.createImageWithBarcode(cb, null, null);
                codeImage.setAlignment(Element.ALIGN_CENTER);
                codeImage.scalePercent(100); // Tamaño normal
                document.add(codeImage);
            } catch (Exception e) { }
        }

        // Espacio si hay ambos
        if (tipoCodigo.equalsIgnoreCase("AMBOS")) {
            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));
        }

        if (tipoCodigo.equalsIgnoreCase("QR") || tipoCodigo.equalsIgnoreCase("AMBOS")) {
            try {
                // QR más pequeño (80x80)
                byte[] qrBytes = GeneradorQR.generarQR(String.valueOf(estudiante.getId()), 80, 80);
                Image qrImage = Image.getInstance(qrBytes);
                qrImage.setAlignment(Element.ALIGN_CENTER);
                document.add(qrImage);
            } catch (Exception e) { }
        }

        // Pie de página
        Paragraph footer = new Paragraph("Si lo encuentra, devuélvalo a Dirección.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 5));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }
}