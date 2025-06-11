package com.cookeasy.prod.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cookeasy.prod.model.User;
import com.cookeasy.prod.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email sudah digunakan.");
        }

        // Simpan user baru
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password salah");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email tidak ditemukan");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        User user = userRepository.findByEmail(updatedUser.getEmail());
        if (user != null) {
            user.setUsername(updatedUser.getUsername());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            return ResponseEntity.ok(userRepository.save(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User tidak ditemukan");
    }

}
