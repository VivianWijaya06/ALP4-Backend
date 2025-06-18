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

    // Tambahkan Levenshtein Distance untuk fuzzy matching typo
    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private boolean containsKeywordFuzzy(String text, String[] keywords) {
        for (String keyword : keywords) {
            // Cek exact
            if (text.contains(keyword)) return true;
            // Cek fuzzy: split text jadi kata, cek tiap kata dengan keyword
            for (String word : text.split("\\s+")) {
                if (levenshtein(word, keyword) <= 2 && word.length() > 3) return true;
            }
        }
        return false;
    }

    @PostMapping
    public ResponseEntity<?> chatWithChefBot(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        if (userMessage == null || userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }

        String lowerMsg = userMessage.toLowerCase();

        // Kata kunci utama (Indonesia & Inggris)
        String[] indoKeywords = {
            "masak", "resep", "makanan", "bahan", "dapur", "memasak", "cara membuat", "tips", "kuliner", "menu", "minuman", "kue", "sayur", "ayam", "daging", "ikan", "telur", "nasi", "mie", "bumbu", "oven", "panggang", "goreng", "tumis", "kukus"
        };
        String[] engKeywords = {
            "cook", "cooking", "recipe", "food", "ingredient", "kitchen", "tips", "culinary", "menu", "drink", "cake", "vegetable", "chicken", "meat", "fish", "egg", "rice", "noodle", "spice", "oven", "bake", "grill", "fry", "stir", "steam"
        };

        boolean isIndo = containsKeywordFuzzy(lowerMsg, indoKeywords);
        boolean isEng = containsKeywordFuzzy(lowerMsg, engKeywords);
        boolean isFoodRelated = isIndo || isEng;

        if (!isFoodRelated) {
            return ResponseEntity.ok(Map.of("reply", "Maaf, ChefBot hanya dapat menjawab pertanyaan seputar masakan, makanan, resep, atau dapur (juga dalam bahasa Inggris)."));
        }

        RestTemplate restTemplate = new RestTemplate();

        // Pilih prompt sesuai bahasa
        String prompt;
        if (isEng && !isIndo) {
            prompt = "You are ChefBot, a friendly cooking assistant. Answer clearly, practically, and enthusiastically. Focus on food, recipes, and kitchen tips.\n\n" + userMessage;
        } else {
            prompt = "Kamu adalah ChefBot, asisten memasak ramah dalam Bahasa Indonesia. Jawab dengan jelas, praktis, dan semangat. Fokus pada makanan, resep, dan tips dapur.\n\n" + userMessage;
        }

        // Gemini expects a "contents" array with "parts" (see Gemini API docs)
        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
            new HashMap<String, Object>() {{
                put("role", "user");
                put("parts", new Object[]{
                    new HashMap<String, Object>() {{
                        put("text", prompt);
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

