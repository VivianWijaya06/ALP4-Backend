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
        User user = userRepository.findByEmail(updatedUser.getEmail());
        if (user == null) throw new IllegalArgumentException("User tidak ditemukan");
        user.setUsername(updatedUser.getUsername());
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
