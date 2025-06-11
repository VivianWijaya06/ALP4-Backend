package com.cookeasy.prod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookeasy.prod.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
}