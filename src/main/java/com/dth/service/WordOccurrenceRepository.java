package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaDelete;

public class WordOccurrenceRepository {
    protected EntityManager em;
    
    public WordOccurrenceRepository(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Using the provided {@code EntityManager}. fetches the list of
     * non-ignored word occurrences.
     * 
     * @param   maxResults  maximum number of entries returned.
     * 
     * @return  the list of word occurrences.
     */
    public List fetchWords(int maxResults) {
        return fetchWords(maxResults, false);
    }
    
    /**
     * Using the provided {@code EntityManager}. fetches the list of
     * word occurrences matching the specified parameters.
     * 
     * @param   maxResults  maximum number of entries returned.
     * @param   ignored     {@code true}, returns only ignored words, while
     *                      {@code false} returns only non-ignored words.
     * 
     * @return  A list of word occurrences
     */
    public List fetchWords(int maxResults, boolean ignored) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(WordOccurrence.class);
        
        Root<WordOccurrence> words = cq.from(WordOccurrence.class);
        cq.select(words);
        cq.orderBy(cb.desc(words.get("count")));
        cq.where(cb.equal(words.get("ignored"), ignored));
        
        TypedQuery<WordOccurrence> query = em.createQuery(cq);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    /**
     * Saves a list of {@code WordOccurrence} objects in the database.
     * 
     * @param   wordOccurrences     list of {@code WordOccurrence}
     *                              object to be saved in the database.
     */
    public void saveWords(List<WordOccurrence> wordOccurrences) {
        // It gets all the results, so it can possibly run out of memory.
        // Grabbing objects one by one takes too much time.
        // TODO: manual iterations with setFirstResult/setMaxResults
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(WordOccurrence.class);
        
        Root<WordOccurrence> words = cq.from(WordOccurrence.class);
        cq.select(words);
        
        TypedQuery<WordOccurrence> query = em.createQuery(cq);
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
    
    /**
     * Removes all word occurrences from the database.
     */
    public void eraseAllWords() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<WordOccurrence> delete = cb.
                createCriteriaDelete(WordOccurrence.class);
        
        delete.from(WordOccurrence.class);
        
        em.createQuery(delete).executeUpdate();
    }
}
