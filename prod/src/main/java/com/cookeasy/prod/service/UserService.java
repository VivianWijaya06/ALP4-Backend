package com.cookeasy.prod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookeasy.prod.model.User;
import com.cookeasy.prod.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email sudah digunakan.");
        }
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new IllegalArgumentException("Email tidak ditemukan");
        if (!user.getPassword().equals(password)) throw new IllegalArgumentException("Password salah");
        return user;
    }

    public User updateProfile(User updatedUser) {
        User user;
        
        // If ID is provided, find by ID, otherwise find by email
        if (updatedUser.getId() != null) {
            user = userRepository.findById(updatedUser.getId()).orElse(null);
        } else {
            user = userRepository.findByEmail(updatedUser.getEmail());
        }
        
        if (user == null) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }
        
        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            User existingUser = userRepository.findByEmail(updatedUser.getEmail());
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email sudah digunakan oleh user lain");
            }
        }
        
        // Update fields
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }
        
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new IllegalArgumentException("User tidak ditemukan");
        return user;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
