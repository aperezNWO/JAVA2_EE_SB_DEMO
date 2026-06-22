package com.example.demo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.FractalManager.FractalKind;
import com.example.demo.FractalManager.FractalPoint;


//
@RestController
@RequestMapping("/api/fractals")
public class FractalController {
 
    private final FractalManager fractalManager = new FractalManager();

    // https://9cdspc-8080.csb.app/api/fractals/generate?kind=2&zoomInOut=false&zoomStep=1
    @GetMapping("/generate")
    public ResponseEntity<List<FractalPoint>> getFractal(
            @RequestParam int kind, // Cambiado temporalmente a int para pruebas
            @RequestParam boolean zoomInOut,
            @RequestParam double zoomStep) {
        
        // Convertimos manualmente usando el método que creamos en el enum
        FractalKind fractalKind = FractalKind.fromValue(kind);
        List<FractalPoint> points = fractalManager.getFractal(fractalKind, zoomInOut, zoomStep);
        return ResponseEntity.ok(points);
    }

    // https://9cdspc-8080.csb.app/api/fractals/health
    @GetMapping("/health")
    public String health() {
        return "Fractal controller works!";
    }
}
