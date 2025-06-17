package com.cookeasy.prod.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private final String uploadDir = "uploads"; // relative to project root

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // Return the URL to access the file
        String fileUrl = "/api/files/" + filename;
        return ResponseEntity.ok().body(fileUrl);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> serveFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
            .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
            .body(Files.readAllBytes(filePath));
    }
}
