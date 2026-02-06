package com.colegio.asistencia.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class GeneradorQR {

    // Metodo estático: No necesitamos instanciar la clase para usarlo
    public static byte[] generarQR(String texto, int ancho, int alto) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Creamos la matriz de bits (el dibujo del QR)
            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

            // Lo convertimos a una imagen en memoria (bytes)
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el QR: " + e.getMessage());
        }
    }
}