package com.dth.service;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class SentenceRepository {

    protected EntityManager em;
    protected CriteriaBuilder cb;

    public SentenceRepository(EntityManager em) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
    }
    
    public List<Sentence> findAll() {
        CriteriaQuery cq = cb.createQuery(Sentence.class);

        Root<Sentence> sentences = cq.from(Sentence.class);
        cq.select(sentences);
        
        return em.createQuery(cq).getResultList();
    }

    public List<Sentence> fetchSentences(int maxresults) {
        CriteriaQuery cq = cb.createQuery(Sentence.class);

        Root<Sentence> sentences = cq.from(Sentence.class);
        cq.select(sentences);

        return em.createQuery(cq).setMaxResults(maxresults).getResultList();
    }

    public List<Sentence> fetchAllSentences() {
        return fetchSentences(Integer.MAX_VALUE);
    }

    public void saveSentences(List<Sentence> sentences, List<WordOccurrence> listOfExistingWords) {
        List<Sentence> currentSentences = findAll();
        
        Map<String, WordOccurrence> existingWords = listOfExistingWords
                .parallelStream()
                .collect(Collectors.toMap(WordOccurrence::getWord, item -> item));
        
        for (Sentence sentence : sentences) {
            if (currentSentences.contains(sentence)) {
                continue;
            }
            
            List<WordOccurrence> oldWords = sentence.getWords();
            List<WordOccurrence> newWords = new ArrayList<>();
            
            for (WordOccurrence oldWord : oldWords) {
                newWords.add(existingWords.get(oldWord.getWord()));
            }
            
            sentence.setWords(newWords);
            em.persist(sentence);
        }
    }

    /**
     * Removes all sentences from the database.
     * 
     * @return the number of sentences deleted.
     */
    public int deleteAllSentences() {
        CriteriaDelete<Sentence> delete = cb.createCriteriaDelete(Sentence.class);
        
        delete.from(Sentence.class);
        
        return em.createQuery(delete).executeUpdate();
    }
}
