package com.cookeasy.prod.dto;

import lombok.Data;

@Data
public class ChefBotMessageDto {
    private String userMessage;
    private String botReply;
    private String userEmail;
    private Long timestamp;
}
