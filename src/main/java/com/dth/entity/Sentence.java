package com.dth.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "SENTENCE")
public class Sentence implements Serializable, Comparable<Sentence> {

    @Id
    @Column(name = "SENTENCE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(name = "SENTENCE", columnDefinition = "CLOB NOT NULL")
    private String sentence;

    @Column(name = "WORDS")
    @ManyToMany
    private List<WordOccurrence> words;

    public Sentence() {
        this("");
    }

    public Sentence(String sentence) {
        this.sentence = sentence;
        this.words = new ArrayList<>();
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    
    public List<WordOccurrence> getWords() {
        return words;
    }
    
    public void setWords(List<WordOccurrence> words) {
        this.words = words;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sentence)) {
            return false;
        }

        Sentence other = (Sentence) object;
        return (this.getSentence().equals(other.getSentence()));
    }

    @Override
    public int compareTo(Sentence other) {
        return this.getSentence().compareTo(other.getSentence());
    }

    @Override
    public String toString() {
        return sentence;
    }
}
