package com.dth.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "WORD_OCCURRENCE")
public class WordOccurrence implements Serializable, Comparable<WordOccurrence> {

    @Id
    @Column(name = "WORD_OCCURRENCE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "WORD")
    private String word;

    @Column(name = "COUNT")
    private int count;

    @Column(name = "IGNORED")
    private boolean ignored;

    @ManyToMany(mappedBy = "words")
    private List<Sentence> sentences;

    public WordOccurrence() {
        this("", 1, false, new ArrayList<>());
    }

    public WordOccurrence(String word) {
        this(word, 1, false, new ArrayList<>());
    }

    public WordOccurrence(String word, int count) {
        this(word, count, false, new ArrayList<>());
    }

    public WordOccurrence(String word, int count, boolean ignored, List<Sentence> sentences) {
        this.word = word;
        this.count = count;
        this.ignored = ignored;
        this.sentences = sentences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public void incrementCount() {
        count++;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Objects are equal if their word and word counts are equal. They may have
     * different or even null IDs.
     *
     * @param object The object.
     * @return True if object's word and count match, otherwise false.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WordOccurrence)) {
            return false;
        }

        WordOccurrence other = (WordOccurrence) object;
        return (this.getWord().equals(other.getWord()))
                && (this.getCount() == other.getCount());
    }

    @Override
    public int compareTo(WordOccurrence other) {
        if (count != other.getCount()) {
            return this.getCount() - other.getCount();
        }

        // If occurences are equal, compare the words  
        return this.getWord().compareTo(other.getWord());
    }

    @Override
    public String toString() {
        return String.format("[%s %d]", word, count);
    }
}
