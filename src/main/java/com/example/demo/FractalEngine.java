package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

//
public class FractalEngine {
    
 
    // 1. Enum modificado con equivalencia numérica
    public enum FractalKind {
        MANDELBROT(1),
        JULIA     (2),
        LEAF      (3);

        private final int value;

        FractalKind(int value) {
            this.value = value;
        }

        // @JsonValue le dice a Jackson que represente este enum como su número entero al enviar datos al cliente
        @JsonValue
        public int getValue() {
            return value;
        }

        // @JsonCreator le dice a Spring/Jackson cómo convertir un número del cliente de vuelta a la constante enum
        @JsonCreator
        public static FractalKind fromValue(int value) {
            for (FractalKind kind : FractalKind.values()) {
                if (kind.value == value) {
                    return kind;
                }
            }
            throw new IllegalArgumentException("Tipo de fractal inválido: " + value);
        }
    }

    // 2. Estructura de puntos (Se mantiene igual, ideal para interactuar con Canvas en Angular)
    public record FractalPoint(double x, double y, int intensity) {}

    //
    /**
     * Orquesta la generación de fractales delegando la lógica mediante un switch.
     */
    public List<FractalPoint> getFractal(FractalKind fractalKind, boolean zoomInOut, double zoomStep) {
        
        // Uso de switch expression (disponible a partir de Java 14+) para un código más limpio
        return switch (fractalKind) {
            case JULIA -> 
                generateJulia(zoomInOut, zoomStep);

            case LEAF  -> 
                // Retorno temporal simulado para Mandelbrot, listo para cuando lo implementemos
                generateLeaf();

            case MANDELBROT -> 
                // Retorno temporal simulado para Mandelbrot, listo para cuando lo implementemos
                List.of(new FractalPoint(0, 0, 0));
                
            default -> 
                throw new IllegalArgumentException("Tipo de fractal no soportado de manera interna.");
        };
   }
   //
   public static List<FractalPoint> generateJulia(boolean zoomInOut, double zoomStep) {
    List<FractalPoint> points = new ArrayList<>();

    // Configuración de la resolución de la cuadrícula
    int width         = 800; 
    int height        = 600;
    int maxIterations = 500;

    // Límites base del plano complejo para Julia
    double minX = -1.5;
    double maxX = 1.5;
    double minY = -1.5;
    double maxY = 1.5;

    // Aplicar factor de zoom
    double zoomFactor = zoomInOut ? (1.0 / zoomStep) : zoomStep;
    double centerX    = 0.0;
    double centerY    = 0.0;
    
    minX = centerX + (minX - centerX) * zoomFactor;
    maxX = centerX + (maxX - centerX) * zoomFactor;
    minY = centerY + (minY - centerY) * zoomFactor;
    maxY = centerY + (maxY - centerY) * zoomFactor;

    // Constante 'c' fija para el conjunto de Julia
    double cRe = -0.400;
    double cIm = 0.600;

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
   //
   /**
     * NEW: Iterated Function System (IFS) Tree Leaf Fractal (kind == 3)
     * Generates a structural leaf shape via linear equations and probability metrics.
     */
   public static List<FractalPoint> generateLeaf() {
    List<FractalPoint> points = new ArrayList<>();
    int width = 800;
    int height = 600;
    
    // 2D Array to serve as a fast pixel map buffer
    int[][] pixelGrid = new int[width][height];
    
    double x = 0.0;
    double y = 0.0;
    Random rand = new Random();
    
    // 150,000 iterations are ideal to densely populate the leaf skeleton without network bloating
    int totalPoints = 150000; 

    for (int i = 0; i < totalPoints; i++) {
        double nextX, nextY;
        int r = rand.nextInt(100);

        // Barnsley/IFS Coefficient Matrix Mapping
        if (r < 1) {
            // Stem transformation
            nextX = 0.0;
            nextY = 0.16 * y;
        } else if (r < 86) {
            // Small leaflets/successive scaling transformation
            nextX = 0.85 * x + 0.04 * y;
            nextY = -0.04 * x + 0.85 * y + 1.6;
        } else if (r < 93) {
            // Left-side leaf expansion transformation
            nextX = 0.20 * x - 0.26 * y;
            nextY = 0.23 * x + 0.22 * y + 1.6;
        } else {
            // Right-side leaf expansion transformation
            nextX = -0.15 * x + 0.28 * y;
            nextY = 0.26 * x + 0.24 * y + 0.44;
        }

        x = nextX;
        y = nextY;

        // Map mathematical bounds (X: -2.182 to 2.655, Y: 0 to 9.96) to screen canvas (800x600)
        int screenX = (int) Math.round((x + 2.182) * (width - 1) / (2.655 + 2.182));
        // Invert Y axis so the leaf grows upwards on HTML Canvas structures
        int screenY = (int) Math.round((9.96 - y) * (height - 1) / 9.96);

        if (screenX >= 0 && screenX < width && screenY >= 0 && screenY < height) {
            // Assign an arbitrary base intensity value (e.g., 200) to represent the leaf tissue density
            pixelGrid[screenX][screenY] = 200; 
        }
    }

    // Convert the structural pixel grid map back to our uniform REST list payload
    for (int px = 0; px < width; px++) {
        for (int py = 0; py < height; py++) {
            if (pixelGrid[px][py] > 0) {
                points.add(new FractalPoint(px, py, pixelGrid[px][py]));
            }
        }
    }

    return points;
  }
}
