package com.example.demo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

//
public class FractalManager {
    
 
    // 1. Enum modificado con equivalencia numérica
    public enum FractalKind {
        MANDELBROT(1),
        JULIA(2);

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
                FractalJulia.generate(zoomInOut, zoomStep);
                
            case MANDELBROT -> 
                // Retorno temporal simulado para Mandelbrot, listo para cuando lo implementemos
                List.of(new FractalPoint(0, 0, 0));
                
            default -> 
                throw new IllegalArgumentException("Tipo de fractal no soportado de manera interna.");
        };
}
}
