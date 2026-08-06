package com.example.demo;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DAO.AccessLogDAO;
import com.example.DAO.personasDAO;
import com.example.demo.FractalEngine.Bounds;
import com.example.demo.FractalEngine.FractalKind;
import com.example.demo.FractalEngine.FractalPoint;
import com.example.entity.accessLog;
import com.example.entity.personaTable;

@RestController
public class DemoEndPoint {

    private final AccessLogDAO accessLogDAO = new AccessLogDAO();
    private final personasDAO _personasDAO = new personasDAO();
    private final FractalEngine fractalEngine = new FractalEngine();

    // ── Unchanged endpoints ───────────────────────────────────────────────────

    @GetMapping("/hello")
    public String hello() {
        Charset utf8Charset = StandardCharsets.UTF_8;
        String text = "Hello, World! &#x25A0; &#x2261;";
        byte[] encodedBytes = text.getBytes(utf8Charset);
        return new String(encodedBytes, utf8Charset);
    }

    @GetMapping("/health")
    public String health() {
        return "Controller Works!";
    }

    @GetMapping("/getAllLogs")
    public ResponseEntity<List<accessLog>> getAllLogs() {
        try {
            return ResponseEntity.ok(accessLogDAO.getAllLogs());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getAllPersons")
    public ResponseEntity<List<personaTable>> getPersons() {
        try {
            return ResponseEntity.ok(_personasDAO.getAllPersons());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/GenerateRandomVertex_SpringBoot_")
    public String GenerateRandomVertex_SpringBoot_() {
        String text = "[2,20]|[15,22]|[1,17]|[8,19]|[14,6]|[13,8]|[5,12]|[4,14]|[22,5]■{0,0,0,6,18,16,0,0,0}|{0,0,14,0,0,0,0,0,18}|{0,14,0,0,0,0,6,0,24}|{6,0,0,0,0,12,0,6,0}|{18,0,0,0,0,0,0,0,0}|{16,0,0,12,0,0,8,0,0}|{0,0,6,0,0,8,0,2,18}|{0,0,0,6,0,0,2,0,0}|{0,18,24,0,0,0,18,0,0}■00&lt;[2;20]&gt;-00-<br/>01&lt;[15;22]&gt;-34-[0;3]≡[3;7]≡[7;6]≡[6;2]≡[2;1]≡<br/>02&lt;[1;17]&gt;-20-[0;3]≡[3;7]≡[7;6]≡[6;2]≡<br/>03&lt;[8;19]&gt;-06-[0;3]≡<br/>04&lt;[14;6]&gt;-18-[0;4]≡<br/>05&lt;[13;8]&gt;-16-[0;5]≡<br/>06&lt;[5;12]&gt;-14-[0;3]≡[3;7]≡[7;6]≡<br/>07&lt;[4;14]&gt;-12-[0;3]≡[3;7]≡<br/>08&lt;[22;5]&gt;-32-[0;3]≡[3;7]≡[7;6]≡[6;8]≡";
        Charset utf8Charset = StandardCharsets.UTF_8;
        byte[] encodedBytes = text.getBytes(utf8Charset);
        return new String(encodedBytes, utf8Charset);
    }

    @GetMapping("/GenerateRandomVertex_SpringBoot")
    public String GenerateRandomVertex_SpringBoot() {
        int p_vertexSize = 9;
        int p_sampleSize = 23;
        int p_sourcePoint = 0;
        try {
            String text = AlgorithmManager.generateRandomPoints(p_vertexSize, p_sampleSize, p_sourcePoint);
            Charset utf8Charset = StandardCharsets.UTF_8;
            byte[] encodedBytes = text.getBytes(utf8Charset);
            return new String(encodedBytes, utf8Charset);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // ── Fractal endpoint (updated) ────────────────────────────────────────────

    /**
     * Generates a fractal and returns a JSON point array consumed by the
     * Angular _fetchAndRender pipeline.
     *
     * Parameters
     * ──────────
     * kind — 1=Mandelbrot, 2=Julia, 3=Barnsley Fern
     * xMin/xMax — complex-plane Re bounds of the current view window.
     * yMin/yMax — complex-plane Im bounds of the current view window.
     * Sent directly by Angular's applyZoomToBounds() — the
     * server no longer derives the window from a zoom
     * step/center transform, it just renders the window
     * it's given. Optional: falls back to each fractal's
     * default view (matching Angular's DEFAULT_BOUNDS_*)
     * when omitted or invalid.
     * maxIterations — escape-time iteration ceiling. Optional, defaults to 500.
     * Ignored for Barnsley Fern (kind=3), which has no
     * escape-time window or iteration ceiling.
     *
     * Examples
     * ────────
     * Default unzoomed Mandelbrot:
     * /api/fractals/generate?kind=1
     *
     * A zoomed Julia view:
     * /api/fractals/generate?kind=2&xMin=-0.7&xMax=0.3&yMin=-0.2&yMax=0.8&maxIterations=500
     */
    @GetMapping("/api/fractals/generate")
    public ResponseEntity<List<FractalPoint>> getFractal(
            @RequestParam int kind,
            @RequestParam(required = false) Double xMin,
            @RequestParam(required = false) Double xMax,
            @RequestParam(required = false) Double yMin,
            @RequestParam(required = false) Double yMax,
            @RequestParam(required = false) Integer maxIterations) {
        FractalKind fractalKind = FractalKind.fromValue(kind);

        // Default bounds per fractal type if the client does not supply them —
        // must match Angular's DEFAULT_BOUNDS_MANDELBROT / DEFAULT_BOUNDS_JULIA.
        Bounds defaultBounds = (fractalKind == FractalKind.MANDELBROT)
                ? new Bounds(-2.0, 1.0, -1.2, 1.2)
                : new Bounds(-1.5, 1.5, -1.5, 1.5);

        boolean boundsSupplied = xMin != null && xMax != null && yMin != null && yMax != null;
        Bounds bounds = boundsSupplied
                ? new Bounds(xMin, xMax, yMin, yMax)
                : defaultBounds;

        int iterations = (maxIterations != null) ? maxIterations : 500;

        List<FractalPoint> points = fractalEngine.getFractal(fractalKind, bounds, iterations);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/getJavaVersion")
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }
}