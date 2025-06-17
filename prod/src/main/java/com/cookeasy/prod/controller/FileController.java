package com.cookeasy.prod.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<?> serveImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, "images", filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
            .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
            .body(Files.readAllBytes(filePath));
    }

    @GetMapping("/audio/{filename:.+}")
    public ResponseEntity<?> serveAudio(@PathVariable String filename, 
                                      @RequestHeader(value = "Range", required = false) String range) throws IOException {
        Path filePath = Paths.get(uploadDir, "audio", filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "audio/mpeg";
        }
        
        File file = filePath.toFile();
        long fileLength = file.length();
        
        if (range == null) {
            // Return the whole file
            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", String.valueOf(fileLength))
                .body(Files.readAllBytes(filePath));
        }
        
        // Handle Range requests
        String[] ranges = range.replace("bytes=", "").split("-");
        long start = 0;
        long end = fileLength - 1;
        
        if (ranges.length > 0 && !ranges[0].isEmpty()) {
            start = Long.parseLong(ranges[0]);
        }
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
            end = Long.parseLong(ranges[1]);
        }
        
        if (start > end || start >= fileLength) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                .header("Content-Range", "bytes */" + fileLength)
                .build();
        }
        
        if (end >= fileLength) {
            end = fileLength - 1;
        }
        
        long contentLength = end - start + 1;
        
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[(int) contentLength];
            randomAccessFile.seek(start);
            randomAccessFile.read(buffer);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", contentType);
            headers.add("Accept-Ranges", "bytes");
            headers.add("Content-Length", String.valueOf(contentLength));
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(buffer);
        }
    }
}
