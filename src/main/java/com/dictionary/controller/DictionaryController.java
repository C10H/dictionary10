package com.dictionary.controller;

import com.dictionary.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dictionary")
@CrossOrigin(origins = "*")
public class DictionaryController {
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @PostMapping("/translate")
    public ResponseEntity<Map<String, Object>> translate(@RequestBody Map<String, String> request) {
        String word = request.get("word");
        
        Map<String, Object> response = new HashMap<>();
        
        if (word == null || word.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Word parameter is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            String translation = dictionaryService.translate(word);
            response.put("success", true);
            response.put("input", word);
            response.put("translation", translation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Translation failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/translate")
    public ResponseEntity<Map<String, Object>> translateGet(@RequestParam String word) {
        Map<String, Object> response = new HashMap<>();
        
        if (word == null || word.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Word parameter is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            String translation = dictionaryService.translate(word);
            response.put("success", true);
            response.put("input", word);
            response.put("translation", translation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Translation failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}