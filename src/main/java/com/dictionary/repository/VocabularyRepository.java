package com.dictionary.repository;

import com.dictionary.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    
    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.english) = LOWER(:word)")
    Optional<Vocabulary> findByEnglishIgnoreCase(@Param("word") String word);
    
    @Query("SELECT v FROM Vocabulary v WHERE v.chinese = :word")
    Optional<Vocabulary> findByChinese(@Param("word") String word);
}