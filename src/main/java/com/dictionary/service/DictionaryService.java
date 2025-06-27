package com.dictionary.service;

import com.dictionary.entity.Vocabulary;
import com.dictionary.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DictionaryService {
    
    @Autowired
    private VocabularyRepository vocabularyRepository;
    
    @Autowired
    private LanguageDetectionService languageDetectionService;
    
    @Autowired
    private BaiduTranslateService baiduTranslateService;
    
    public String translate(String word) {
        if (word == null || word.trim().isEmpty()) {
            return "Empty input";
        }
        
        word = word.trim();
        
        if (languageDetectionService.isEnglish(word)) {
            return translateEnglishToChinese(word);
        } else if (languageDetectionService.isChinese(word)) {
            return translateChineseToEnglish(word);
        } else {
            return "Unsupported language";
        }
    }
    
    private String translateEnglishToChinese(String englishWord) {
        Optional<Vocabulary> vocab = vocabularyRepository.findByEnglishIgnoreCase(englishWord);
        if (vocab.isPresent()) {
            return vocab.get().getChinese();
        }
        
        try {
            return baiduTranslateService.translate(englishWord, "en", "zh");
        } catch (Exception e) {
            return "Translation not found";
        }
    }
    
    private String translateChineseToEnglish(String chineseWord) {
        Optional<Vocabulary> vocab = vocabularyRepository.findByChinese(chineseWord);
        if (vocab.isPresent()) {
            return vocab.get().getEnglish();
        }
        
        try {
            return baiduTranslateService.translate(chineseWord, "zh", "en");
        } catch (Exception e) {
            return "Translation not found";
        }
    }
}