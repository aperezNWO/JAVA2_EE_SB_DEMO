package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class FractalEngine {

    // ─────────────────────────────────────────────────────────────────────────
    // FRACTAL KIND ENUM
    // ─────────────────────────────────────────────────────────────────────────

    public enum FractalKind {
        MANDELBROT(1),
        JULIA(2),
        LEAF(3);

        private final int value;

        FractalKind(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static FractalKind fromValue(int value) {
            for (FractalKind kind : FractalKind.values()) {
                if (kind.value == value)
                    return kind;
            }
            throw new IllegalArgumentException("Tipo de fractal inválido: " + value);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SHARED TYPES
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Wire format returned to Angular.
     * x, y — pixel coordinates (integers, typed as double for
     * backwards-compatibility with existing consumers)
     * intensity — [0…255] encoding of the escape-time iteration count:
     * 0 → point is inside the set (maxIterations reached)
     * 1…255 → iter * 255 / maxIterations
     * Angular's _adaptRemotePoints back-calculates the iteration
     * from this value, so the formula must stay consistent.
     */
    public record FractalPoint(double x, double y, int intensity) {
    }

    /**
     * Complex-plane view window shared by all escape-time fractals.
     * Sent directly by Angular's applyZoomToBounds() — the server no longer
     * derives bounds from a zoomStep/center transform, it just renders
     * whatever window it's given. Mirrors FractalBounds on the Angular side.
     */
    public record Bounds(double xMin, double xMax, double yMin, double yMax) {
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CANVAS DIMENSIONS — single source of truth, must match Angular constants
    // ─────────────────────────────────────────────────────────────────────────
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int MAX_ITERATIONS = 500;

    // ─────────────────────────────────────────────────────────────────────────
    // SHARED INTENSITY ENCODING
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Encodes the escape-time iteration count as an [0…255] intensity value.
     *
     * Must stay in sync with _adaptRemotePoints() in the Angular service:
     * Angular: value = round(intensity * maxIterations / 255)
     * Java: intensity = (iter == maxIterations) ? 0 : (iter * 255 / maxIterations)
     *
     * Special case: iter == maxIterations means the point is INSIDE the set
     * → intensity 0 → Angular maps this back to maxIterations → black pixel.
     */
    private static int encodeIntensity(int iter, int maxIterations) {
        return (iter == maxIterations) ? 0 : (iter * 255 / maxIterations);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROUTER
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Orchestrates fractal generation, delegating to the correct engine
     * by fractal kind. Bounds/maxIterations are ignored for LEAF (IFS
     * scatter has no escape-time window or iteration ceiling).
     */
    public List<FractalPoint> getFractal(
            FractalKind fractalKind,
            Bounds bounds,
            int maxIterations) {
        return switch (fractalKind) {
            case MANDELBROT -> generateMandelbrot(bounds, maxIterations);
            case JULIA -> generateJulia(bounds, maxIterations);
            case LEAF -> generateLeaf();
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MANDELBROT (new)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates the Mandelbrot set on a CANVAS_WIDTH × CANVAS_HEIGHT grid.
     *
     * Formula: z(n+1) = z(n)² + c, z(0) = 0, c = pixel coordinate
     *
     * Bounds are sent directly by Angular's applyZoomToBounds() — no
     * server-side zoom transform. DEFAULT_BOUNDS_MANDELBROT on the Angular
     * side (Re ∈ [-2.0, 1.0], Im ∈ [-1.2, 1.2]) is what the client falls
     * back to when unzoomed, so both ends agree on the default view.
     *
     * @param bounds        complex-plane view window (xMin/xMax/yMin/yMax)
     * @param maxIterations escape-time iteration ceiling, supplied by the
     *                      client so it stays in sync with _adaptRemotePoints
     */
    public static List<FractalPoint> generateMandelbrot(Bounds bounds, int maxIterations) {
        List<FractalPoint> points = new ArrayList<>();

        double xRange = bounds.xMax() - bounds.xMin();
        double yRange = bounds.yMax() - bounds.yMin();

        for (int screenY = 0; screenY < CANVAS_HEIGHT; screenY++) {
            for (int screenX = 0; screenX < CANVAS_WIDTH; screenX++) {

                // Map pixel → complex plane coordinate
                double cRe = bounds.xMin() + (screenX * xRange / CANVAS_WIDTH);
                double cIm = bounds.yMin() + (screenY * yRange / CANVAS_HEIGHT);

                // Mandelbrot iteration: z starts at 0, c = pixel coordinate
                double zRe = 0.0, zIm = 0.0;
                int iter = 0;

                while (zRe * zRe + zIm * zIm <= 4.0 && iter < maxIterations) {
                    double nextRe = zRe * zRe - zIm * zIm + cRe;
                    double nextIm = 2.0 * zRe * zIm + cIm;
                    zRe = nextRe;
                    zIm = nextIm;
                    iter++;
                }

                points.add(new FractalPoint(screenX, screenY, encodeIntensity(iter, maxIterations)));
            }
        }

        return points;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JULIA (updated — zoom now uses centerX / centerY)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates the Julia set on a CANVAS_WIDTH × CANVAS_HEIGHT grid.
     *
     * Formula: z(n+1) = z(n)² + c, z(0) = pixel coordinate, c = fixed constant
     *
     * Bounds are sent directly by Angular's applyZoomToBounds() — no
     * server-side zoom transform. DEFAULT_BOUNDS_JULIA on the Angular side
     * (Re ∈ [-1.5, 1.5], Im ∈ [-1.5, 1.5]) is what the client falls back to
     * when unzoomed, so both ends agree on the default view.
     *
     * @param bounds        complex-plane view window (xMin/xMax/yMin/yMax)
     * @param maxIterations escape-time iteration ceiling, supplied by the
     *                      client so it stays in sync with _adaptRemotePoints
     */
    public static List<FractalPoint> generateJulia(Bounds bounds, int maxIterations) {
        List<FractalPoint> points = new ArrayList<>();

        double xRange = bounds.xMax() - bounds.xMin();
        double yRange = bounds.yMax() - bounds.yMin();

        // Fixed complex constant c — unchanged from original
        double cRe = -0.400;
        double cIm = 0.600;

        for (int screenY = 0; screenY < CANVAS_HEIGHT; screenY++) {
            for (int screenX = 0; screenX < CANVAS_WIDTH; screenX++) {

                // Map pixel → complex plane coordinate
                // Julia: z starts at the pixel coordinate, c is fixed
                double zRe = bounds.xMin() + (screenX * xRange / CANVAS_WIDTH);
                double zIm = bounds.yMin() + (screenY * yRange / CANVAS_HEIGHT);

                int iter = 0;
                while (zRe * zRe + zIm * zIm <= 4.0 && iter < maxIterations) {
                    double nextRe = zRe * zRe - zIm * zIm + cRe;
                    double nextIm = 2.0 * zRe * zIm + cIm;
                    zRe = nextRe;
                    zIm = nextIm;
                    iter++;
                }

                points.add(new FractalPoint(screenX, screenY, encodeIntensity(iter, maxIterations)));
            }
        }

        return points;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BARNSLEY FERN (unchanged)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * IFS Barnsley Fern — zoom does not apply to IFS attractors.
     * Unchanged from the original implementation.
     */
    public static List<FractalPoint> generateLeaf() {
        List<FractalPoint> points = new ArrayList<>();
        int[][] pixelGrid = new int[CANVAS_WIDTH][CANVAS_HEIGHT];

        double x = 0.0, y = 0.0;
        Random rand = new Random();
        int totalPoints = 150_000;

        for (int i = 0; i < totalPoints; i++) {
            double nextX, nextY;
            int r = rand.nextInt(100);

            if (r < 1) {
                nextX = 0.0;
                nextY = 0.16 * y;
            } else if (r < 86) {
                nextX = 0.85 * x + 0.04 * y;
                nextY = -0.04 * x + 0.85 * y + 1.6;
            } else if (r < 93) {
                nextX = 0.20 * x - 0.26 * y;
                nextY = 0.23 * x + 0.22 * y + 1.6;
            } else {
                nextX = -0.15 * x + 0.28 * y;
                nextY = 0.26 * x + 0.24 * y + 0.44;
            }

            x = nextX;
            y = nextY;

            int screenX = (int) Math.round((x + 2.182) * (CANVAS_WIDTH - 1) / (2.655 + 2.182));
            int screenY = (int) Math.round((9.96 - y) * (CANVAS_HEIGHT - 1) / 9.96);

            if (screenX >= 0 && screenX < CANVAS_WIDTH && screenY >= 0 && screenY < CANVAS_HEIGHT) {
                pixelGrid[screenX][screenY] = 200;
            }
        }

        for (int px = 0; px < CANVAS_WIDTH; px++) {
            for (int py = 0; py < CANVAS_HEIGHT; py++) {
                if (pixelGrid[px][py] > 0) {
                    points.add(new FractalPoint(px, py, pixelGrid[px][py]));
                }
            }
        }

        return points;
    }
}