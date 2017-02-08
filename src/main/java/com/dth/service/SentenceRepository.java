package com.dth.service;

import com.dth.entity.Sentence;
import com.dth.entity.Sentence_;
import com.dth.entity.WordOccurrence;
import java.util.List;
import java.util.Optional;
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

    public List<Sentence> fetchSentences(int maxresults) {
        CriteriaQuery cq = cb.createQuery(Sentence.class);

        Root<Sentence> sentences = cq.from(Sentence.class);
        cq.select(sentences);

        return em.createQuery(cq).setMaxResults(maxresults).getResultList();
    }

    public List<Sentence> fetchAllSentences() {
        return fetchSentences(Integer.MAX_VALUE);
    }

    public void saveSentences(List<Sentence> sentences) {
        // It gets all the results, so it can possibly run out of memory.
        // Grabbing objects one by one takes too much time.
        // TODO: manual iterations with setFirstResult/setMaxResults
        CriteriaQuery cq = cb.createQuery(Sentence.class);

        Root<Sentence> words = cq.from(Sentence.class);
        cq.select(words);

        List<Sentence> existingSentences = em.createQuery(cq).getResultList();

        sentences.forEach(s -> {
            Optional<Sentence> result = existingSentences.parallelStream()
                    .filter(exs -> exs.equals(s))
                    .findAny();

            if (!result.isPresent()) {
                em.persist(s);
            }
        });
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
        
    /**
     * Erases all empty sentences from the database.
     * 
     * @return the number of sentences deleted.
     */
    public int deleteEmptySentences() {
        CriteriaDelete deleteSentences = cb.createCriteriaDelete(Sentence.class);
        
        Root<Sentence> sentence = deleteSentences.from(WordOccurrence.class);
        deleteSentences.where(cb.equal(sentence.get(Sentence_.words), null));
        
        return em.createQuery(deleteSentences).executeUpdate();
    }
}
