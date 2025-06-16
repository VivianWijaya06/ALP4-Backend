package com.cookeasy.prod.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chefbot_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChefBotMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userMessage;

    @Column(length = 2000)
    private String botReply;

    private String userEmail; // optional, for associating with a user

    private Long timestamp;
}
