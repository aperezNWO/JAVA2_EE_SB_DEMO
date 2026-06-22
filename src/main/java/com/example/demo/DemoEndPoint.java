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
import com.example.demo.FractalManager.FractalKind;
import com.example.demo.FractalManager.FractalPoint;
import com.example.entity.accessLog;
import com.example.entity.personaTable;

@RestController
public class DemoEndPoint {
    //
    private final AccessLogDAO accessLogDAO = new AccessLogDAO();
    private final personasDAO _personasDAO = new personasDAO();
    //
    private final FractalManager fractalManager = new FractalManager();


    //
    @GetMapping("/hello")
    public String hello() {

        // String originalString = "Hello, World! & ? / ■";
        // String encodedString = URLEncoder.encode(originalString,
        // StandardCharsets.UTF_8);

        // return encodedString;
        // return originalString;

        // Get a Charset object for UTF-8 encoding
        Charset utf8Charset = StandardCharsets.UTF_8;

        // Encode a string into a byte array
        String text = "Hello, World! &#x25A0; &#x2261;";

        byte[] encodedBytes = text.getBytes(utf8Charset);

        // Decode a byte array into a string
        String decodedText = new String(encodedBytes, utf8Charset);

        return decodedText;
    }

    // https://9cdspc-8080.csb.app/health
    @GetMapping("/health")
    public String health() {
        return "Fractal controller works!";
    }
    
    // Get All LOGS
    @GetMapping("/getAllLogs")
    public ResponseEntity<List<accessLog>> getAllLogs() {
        try {
            //
            List<accessLog> accessLog = accessLogDAO.getAllLogs();
            return ResponseEntity.ok(accessLog);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get All PERSONS
    @GetMapping("/getAllPersons")
    public ResponseEntity<List<personaTable>> getPersons() {
        try {
            //
            List<personaTable> personas = _personasDAO.getAllPersons();
            return ResponseEntity.ok(personas);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    //
    @GetMapping("/GenerateRandomVertex_SpringBoot_")
    public String GenerateRandomVertex_SpringBoot_() {

        // Encode a string into a byte array
        // String text = "Hello, World! &#x25A0; &#x2261;";

        String text = "[2,20]|[15,22]|[1,17]|[8,19]|[14,6]|[13,8]|[5,12]|[4,14]|[22,5]■{0,0,0,6,18,16,0,0,0}|{0,0,14,0,0,0,0,0,18}|{0,14,0,0,0,0,6,0,24}|{6,0,0,0,0,12,0,6,0}|{18,0,0,0,0,0,0,0,0}|{16,0,0,12,0,0,8,0,0}|{0,0,6,0,0,8,0,2,18}|{0,0,0,6,0,0,2,0,0}|{0,18,24,0,0,0,18,0,0}■00&lt;[2;20]&gt;-00-<br/>01&lt;[15;22]&gt;-34-[0;3]≡[3;7]≡[7;6]≡[6;2]≡[2;1]≡<br/>02&lt;[1;17]&gt;-20-[0;3]≡[3;7]≡[7;6]≡[6;2]≡<br/>03&lt;[8;19]&gt;-06-[0;3]≡<br/>04&lt;[14;6]&gt;-18-[0;4]≡<br/>05&lt;[13;8]&gt;-16-[0;5]≡<br/>06&lt;[5;12]&gt;-14-[0;3]≡[3;7]≡[7;6]≡<br/>07&lt;[4;14]&gt;-12-[0;3]≡[3;7]≡<br/>08&lt;[22;5]&gt;-32-[0;3]≡[3;7]≡[7;6]≡[6;8]≡";

        // Get a Charset object for UTF-8 encoding
        Charset utf8Charset = StandardCharsets.UTF_8;

        byte[] encodedBytes = text.getBytes(utf8Charset);

        // Decode a byte array into a string
        String decodedText = new String(encodedBytes, utf8Charset);

        return decodedText;
    }
    
    //
    @GetMapping("/GenerateRandomVertex_SpringBoot")
    public String GenerateRandomVertex_SpringBoot() {

        //
        int p_vertexSize = 9;
        int p_sampleSize = 23;
        int p_sourcePoint = 0;
        //
        try {
            //
            String text = AlgorithmManager.generateRandomPoints(p_vertexSize, p_sampleSize, p_sourcePoint);

            // Get a Charset object for UTF-8 encoding
            Charset utf8Charset = StandardCharsets.UTF_8;
           
            //
            byte[] encodedBytes = text.getBytes(utf8Charset);

            // Decode a byte array into a string
            String decodedText = new String(encodedBytes, utf8Charset);
            //
            return  decodedText;

        } catch (Exception e) {
            //
            String decodedText = e.getMessage();
            return decodedText;    
        }
    }


    // https://9cdspc-8080.csb.app/api/fractals/generate?kind=2&zoomInOut=false&zoomStep=1
    @GetMapping("/api/fractals/generate")
    public ResponseEntity<List<FractalPoint>> getFractal(
            @RequestParam int kind, // Cambiado temporalmente a int para pruebas
            @RequestParam boolean zoomInOut,
            @RequestParam double zoomStep) {
        
        // Convertimos manualmente usando el método que creamos en el enum
        FractalKind fractalKind = FractalKind.fromValue(kind);
        List<FractalPoint> points = fractalManager.getFractal(fractalKind, zoomInOut, zoomStep);
        return ResponseEntity.ok(points);
    }

    
}
