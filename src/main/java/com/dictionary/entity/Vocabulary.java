package com.dictionary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vocabulary")
public class Vocabulary {
    
    @Id
    @Column(name = "english")
    private String english;
    
    @Column(name = "chinese")
    private String chinese;
    
    public Vocabulary() {}
    
    public Vocabulary(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
    }
    
    public String getEnglish() {
        return english;
    }
    
    public void setEnglish(String english) {
        this.english = english;
    }
    
    public String getChinese() {
        return chinese;
    }
    
    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}