package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.FractalManager.FractalPoint;


public class FractalJulia {
    public static List<FractalPoint> generate(boolean zoomInOut, double zoomStep) {
        List<FractalPoint> points = new ArrayList<>();

        // Configuración de la resolución de la cuadrícula
        int width = 300; 
        int height = 300;
        int maxIterations = 100;

        // Límites base del plano complejo para Julia
        double minX = -1.5;
        double maxX = 1.5;
        double minY = -1.5;
        double maxY = 1.5;

        // Aplicar factor de zoom
        double zoomFactor = zoomInOut ? (1.0 / zoomStep) : zoomStep;
        double centerX = 0.0;
        double centerY = 0.0;
        
        minX = centerX + (minX - centerX) * zoomFactor;
        maxX = centerX + (maxX - centerX) * zoomFactor;
        minY = centerY + (minY - centerY) * zoomFactor;
        maxY = centerY + (maxY - centerY) * zoomFactor;

        // Constante 'c' fija para el conjunto de Julia
        double cRe = -0.7;
        double cIm = 0.27015;

        // Evaluar la cuadrícula
        for (int screenX = 0; screenX < width; screenX++) {
            for (int screenY = 0; screenY < height; screenY++) {
                
                double zRe = minX + (screenX * (maxX - minX) / width);
                double zIm = minY + (screenY * (maxY - minY) / height);

                int iter = 0;
                while (zRe * zRe + zIm * zIm <= 4.0 && iter < maxIterations) {
                    double nextRe = zRe * zRe - zIm * zIm + cRe;
                    double nextIm = 2.0 * zRe * zIm + cIm;
                    
                    zRe = nextRe;
                    zIm = nextIm;
                    iter++;
                }

                int intensity = (iter == maxIterations) ? 0 : (iter * 255 / maxIterations);
                points.add(new FractalPoint(screenX, screenY, intensity));
            }
        }

        return points;
    }
}
