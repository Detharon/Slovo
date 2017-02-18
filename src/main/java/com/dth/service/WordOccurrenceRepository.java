package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaDelete;

public class WordOccurrenceRepository {

    protected EntityManager em;
    protected CriteriaBuilder cb;

    public WordOccurrenceRepository(EntityManager em) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
    }

    /**
     * Returns all word occurrences stored in the database.
     *
     * @return the list of word occurrences.
     */
    public List<WordOccurrence> findAll() {
        CriteriaQuery cq = cb.createQuery(WordOccurrence.class);
        Root<WordOccurrence> words = cq.from(WordOccurrence.class);
        cq.select(words);
        TypedQuery<WordOccurrence> query = em.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Using the provided {@code EntityManager}. fetches the list of non-ignored
     * word occurrences.
     *
     * @param maxResults maximum number of entries returned.
     *
     * @return the list of word occurrences.
     */
    public List fetchWords(int maxResults) {
        return fetchWords(maxResults, false);
    }

    /**
     * Using the provided {@code EntityManager}. fetches the list of word
     * occurrences matching the specified parameters.
     *
     * @param maxResults maximum number of entries returned.
     * @param ignored {@code true}, returns only ignored words, while
     * {@code false} returns only non-ignored words.
     *
     * @return A list of word occurrences
     */
    public List fetchWords(int maxResults, boolean ignored) {
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
     * @param wordOccurrences list of {@code WordOccurrence} object to be saved
     * in the database.
     */
    public void saveWords(List<WordOccurrence> wordOccurrences) {
        // It gets all the results, so it can possibly run out of memory.
        // Grabbing objects one by one takes too much time.
        // TODO: manual iterations with setFirstResult/setMaxResults
        // List<WordOccurrence> listOfExistingWords = findAllAndFetchSentences();

        List<WordOccurrence> listOfExistingWords = findAll();

        Map<String, WordOccurrence> existingWords = listOfExistingWords
                .parallelStream()
                .collect(Collectors.toMap(WordOccurrence::getWord, item -> item));

        for (WordOccurrence newWord : wordOccurrences) {
            Optional<WordOccurrence> word = Optional.ofNullable(existingWords.get(newWord.getWord()));

            if (word.isPresent()) {
                // Merge the second second word into the first one, which is managed by entity manager
                WordOccurrence existingWord = word.get();
                existingWord.setCount(existingWord.getCount() + newWord.getCount());
            } else {
                em.persist(newWord);
            }
        }
    }

    public void updateWords(List<WordOccurrence> words) {
        for (WordOccurrence word : words) {
            em.merge(word);
        }
    }

    /**
     * Removes all words from the database.
     *
     * @return the number of words deleted.
     */
    public int deleteAllWords() {
        CriteriaDelete<WordOccurrence> delete = cb.createCriteriaDelete(WordOccurrence.class);

        delete.from(WordOccurrence.class);

        return em.createQuery(delete).executeUpdate();
    }
}
