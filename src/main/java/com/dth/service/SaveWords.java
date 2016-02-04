package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class SaveWords {
    private EntityManager em;
    
    public SaveWords(EntityManager em) {
        this.em = em;
    }
    
    public void execute(List<WordOccurrence> wordOccurrences) {
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
}
