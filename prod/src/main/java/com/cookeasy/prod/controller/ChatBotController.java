package com.cookeasy.prod.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    @PostMapping
    public ResponseEntity<?> chatWithChefBot(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        if (userMessage == null || userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }

        // Filter: hanya izinkan pertanyaan tentang masakan/makanan
        String lowerMsg = userMessage.toLowerCase();
        String[] allowedKeywords = {
            "masak", "resep", "makanan", "bahan", "dapur", "memasak", "cara membuat", "tips", "kuliner", "menu", "minuman", "kue", "sayur", "ayam", "daging", "ikan", "telur", "nasi", "mie", "bumbu", "oven", "panggang", "goreng", "tumis", "kukus", "boil", "bake", "cook", "food", "recipe", "kitchen"
        };
        boolean isFoodRelated = false;
        for (String keyword : allowedKeywords) {
            if (lowerMsg.contains(keyword)) {
                isFoodRelated = true;
                break;
            }
        }
        if (!isFoodRelated) {
            return ResponseEntity.ok(Map.of("reply", "Maaf, ChefBot hanya dapat menjawab pertanyaan seputar masakan, makanan, resep, atau dapur."));
        }

        RestTemplate restTemplate = new RestTemplate();

        // Gemini expects a "contents" array with "parts" (see Gemini API docs)
        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
            new HashMap<String, Object>() {{
                put("role", "user");
                put("parts", new Object[]{
                    new HashMap<String, Object>() {{
                        put("text", "Kamu adalah ChefBot, asisten memasak ramah dalam Bahasa Indonesia. Jawab dengan jelas, praktis, dan semangat. Fokus pada makanan, resep, dan tips dapur.\n\n" + userMessage);
                    }}
                });
            }}
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            String url = GEMINI_URL + "?key=" + geminiApiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Gemini's response: { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
            Map bodyMap = response.getBody();
            String reply = "";
            if (bodyMap != null && bodyMap.containsKey("candidates")) {
                Object[] candidates = ((java.util.List<Object>) bodyMap.get("candidates")).toArray();
                if (candidates.length > 0) {
                    Map candidate = (Map) candidates[0];
                    Map content = (Map) candidate.get("content");
                    java.util.List<Map> parts = (java.util.List<Map>) content.get("parts");
                    if (!parts.isEmpty()) {
                        reply = (String) parts.get(0).get("text");
                    }
                }
            }
            if (reply.isEmpty()) reply = "Maaf, ChefBot tidak dapat menjawab saat ini.";
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menghubungi ChefBot.");
        }
    }
}

