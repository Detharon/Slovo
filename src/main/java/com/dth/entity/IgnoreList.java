package com.dth.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class IgnoreList implements Serializable {
    @Id
    @Column(name = "IGNORE_LIST_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToMany
    @JoinColumn(name = "WORD")
    private Set<WordOccurrence> words;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addWord(WordOccurrence word) {
        words.add(word);
    }
    
    public void removeWord(WordOccurrence word) {
        if (words.contains(word)) {
            words.remove(word);
        }
    }
}
