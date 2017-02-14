package com.dth.util;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultSentenceProcessor implements SentenceProcessor<Sentence, WordOccurrence> {

    public WordProcessor<String> wordProcessor;
    public List<WordOccurrence> words;

    public DefaultSentenceProcessor(WordProcessor<String> wordProcessor) {
        this.wordProcessor = wordProcessor;
        this.words = new LinkedList<>();
    }

    @Override
    public void processSentence(Sentence sentence) {
        String[] wordCandidates = sentence.getSentence().split(" +");

        for (String wordCandidate : wordCandidates) {
            String word = wordProcessor.processWord(wordCandidate);
            if (word.equals("")) {
                continue;
            }

            Optional<WordOccurrence> existingWordOccurrence = words.stream()
                    .filter(w -> w.getWord().equals(word))
                    .findAny();

            if (existingWordOccurrence.isPresent()) {
                WordOccurrence wordOccurrence = existingWordOccurrence.get();

                wordOccurrence.incrementCount();
                if (!wordOccurrence.getSentences().contains(sentence)) {
                    wordOccurrence.getSentences().add(sentence);
                    if (!sentence.getWords().contains(wordOccurrence)) {
                        sentence.getWords().add(wordOccurrence);
                    }
                }
            } else {
                WordOccurrence wordOccurrence = new WordOccurrence(word);
                wordOccurrence.getSentences().add(sentence);
                sentence.getWords().add(wordOccurrence);
                words.add(wordOccurrence);
            }
        }
    }

    @Override
    public List<WordOccurrence> getWords() {
        return words;
    }
}
