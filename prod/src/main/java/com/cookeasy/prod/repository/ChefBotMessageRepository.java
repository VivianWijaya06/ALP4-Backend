package com.cookeasy.prod.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookeasy.prod.model.ChefBotMessage;

public interface ChefBotMessageRepository extends JpaRepository<ChefBotMessage, Long> {
    List<ChefBotMessage> findByUserEmailOrderByTimestampDesc(String userEmail);
}
