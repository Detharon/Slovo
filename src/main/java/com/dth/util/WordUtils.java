package com.dth.util;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import java.util.List;

public class WordUtils {

    /**
     * Merges two words together. The sentences and occurrences count will be
     * copied from the second word to the first word.<br>
     *
     * As a result, the first word will contain information from both words.
     *
     * @param word1 the first word (will be modified).
     * @param word2 the second word
     */
    public static void merge(WordOccurrence word1, WordOccurrence word2) {
        // Add the occurrences
        word1.setCount(word1.getCount() + word2.getCount());

        List<Sentence> newSentences = word2.getSentences();
        for (Sentence newSentence : newSentences) {
            newSentence.getWords().remove(word2);
            newSentence.getWords().add(word1);

            if (!word1.getSentences().contains(newSentence)) {
                word1.getSentences().add(newSentence);
            }
        }
    }
}
