package com.cookeasy.prod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookeasy.prod.model.ChefBotMessage;
import com.cookeasy.prod.repository.ChefBotMessageRepository;

@Service
public class ChefBotService {

    @Autowired
    private ChefBotMessageRepository chefBotMessageRepository;

    public ChefBotMessage saveMessage(ChefBotMessage message) {
        return chefBotMessageRepository.save(message);
    }

    public List<ChefBotMessage> getMessagesByUserEmail(String userEmail) {
        return chefBotMessageRepository.findByUserEmailOrderByTimestampDesc(userEmail);
    }
}
