package com.dictionary.service;

import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {
    
    public boolean isChinese(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEnglish(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        return text.matches("^[a-zA-Z\\s]+$");
    }
}