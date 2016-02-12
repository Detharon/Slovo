package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class WordOccurrenceRepository {
    protected EntityManager em;
    
    public WordOccurrenceRepository(EntityManager em) {
        this.em = em;
    }
    
    public List fetchWords(int maxResults) {
        Query query = em.createQuery("SELECT e FROM WordOccurrence e "
                + "WHERE e.ignored = false ORDER BY e.count DESC");
        query.setMaxResults(maxResults);
   
        return query.getResultList();
    }
    
    public List fetchIgnoredWords(int maxResults) {
        Query query = em.createQuery("SELECT e FROM WordOccurrence e "
                + "WHERE e.ignored = true ORDER BY e.count DESC");
        
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    public void saveWords(List<WordOccurrence> wordOccurrences) {
        // It gets all the results, so it can possibly run out of memory.
        // Grabbing objects one by one takes too much time.
        // TODO: manual iterations with setFirstResult/setMaxResults
        Query query = em.createQuery("SELECT e FROM WordOccurrence e");
        List<WordOccurrence> results = query.getResultList();
        
        for (WordOccurrence wordOccurrence : wordOccurrences) {
            Optional<WordOccurrence> result = results.parallelStream()
                    .filter(c -> c.getWord().equals(wordOccurrence.getWord()))
                    .findFirst();

            if (result.isPresent()) {
                result.get().setCount(result.get().getCount()+wordOccurrence.getCount());
                em.merge(result.get());
            } else {
                em.merge(wordOccurrence);
            }
        }
    }
    
    public void updateWords(List<WordOccurrence> words) {
        for (WordOccurrence word : words) {
            em.merge(word);
        }
    }
    
    public void eraseAllWords() {
        Query query = em.createQuery("DELETE FROM WordOccurrence");
        query.executeUpdate();
    }
}
