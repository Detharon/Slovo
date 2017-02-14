package com.dth.util;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSentenceProcessorTest {

    private static DefaultSentenceProcessor processor;
    private static WordProcessor wp;

    private static final String[] SENTENCES = new String[]{
        "Any road followed precisely to its end leads precisely nowhere.",
        "Climb the mountain just a little bit to test that it's a mountain.",
        "From the top of the mountain, you cannot see the mountain.",
        "A beginning is the time for taking the most delicate care that the balances are correct."
    };

    @BeforeClass
    public static void setUpClass() {
        wp = mock(WordProcessor.class);

        mockProcessorForFirstSentence(wp);
        mockProcessorForSecondSentence(wp);
        mockProcessorForThirdSentence(wp);
        mockProcessorForFourthSentence(wp);
    }

    @Before
    public void setUp() throws URISyntaxException {
        // We want a new instance for each test, because the SentenceProcessor
        // is not stateless - it keeps a list of words.
        processor = new DefaultSentenceProcessor(wp);
    }

    /**
     * Test of processSentence method, of class DefaultSentenceProcessor.
     */
    @Test
    public void testProcessSentence() {
        processor.processSentence(new Sentence(SENTENCES[0]));
        processor.processSentence(new Sentence(SENTENCES[1]));
        processor.processSentence(new Sentence(SENTENCES[2]));
        processor.processSentence(new Sentence(SENTENCES[3]));

        List expectedWords = getAllSentencesWords();
        List resultWords = processor.getWords();

        assertThat(expectedWords, is(resultWords));
    }

    /**
     * Test of processSentence method, of class DefaultSentenceProcessor. Makes
     * sure that the resultant word has a correct sentence.
     */
    @Test
    public void testProcessSentenceResultantWord() {
        Sentence sentence = new Sentence(SENTENCES[3]);
        processor.processSentence(sentence);
        List<WordOccurrence> resultWords = processor.getWords();

        // Trying to find a "the" word with 3 occurrences
        WordOccurrence expectedWord = resultWords.stream()
                .filter(w -> w.getWord().equals("the") && w.getCount() == 3)
                .findAny()
                .get();

        // There should be only one sentence in the "the" word - the initial sentence
        assertTrue(expectedWord.getSentences().size() == 1
                && expectedWord.getSentences().get(0).equals(sentence));
    }

    /**
     * Test of processSentence method, of class DefaultSentenceProcessor. Makes
     * sure that the resultant sentence has the correct words.
     */
    @Test
    public void testProcessSentenceResultantSentence() {
        Sentence sentence = new Sentence(SENTENCES[0]);
        processor.processSentence(sentence);

        List<WordOccurrence> wordsExtracted = sentence.getWords();
        List<WordOccurrence> wordsExpected = getFirstSentenceWords();
        assertThat(wordsExtracted, is(wordsExpected));
    }

    // --------------------------------------------------
    // Helper methods
    // --------------------------------------------------
    /**
     * Mocks the word processor behavior for the following sentence:<br>
     * "Any road followed precisely to its end leads precisely nowhere."
     *
     * @param wp the {@code WordProcessor}.
     */
    private static void mockProcessorForFirstSentence(WordProcessor wp) {
        String[] input = new String[]{"Any", "road", "followed", "precisely", "to", "its",
            "end", "leads", "precisely", "nowhere."};

        String[] output = new String[]{"any", "road", "followed", "precisely", "to", "its",
            "end", "leads", "precisely", "nowhere"};

        for (int i = 0; i < input.length; i++) {
            when(wp.processWord(input[i])).thenReturn(output[i]);
        }
    }

    /**
     * Mocks the word processor behavior for the following sentence:<br>
     * "Climb the mountain just a little bit to test that it's a mountain."
     *
     * @param wp the {@code WordProcessor}.
     */
    private static void mockProcessorForSecondSentence(WordProcessor wp) {
        String[] input = new String[]{"Climb", "the", "mountain", "just", "a", "little",
            "bit", "to", "test", "that", "it's", "a", "mountain."};

        String[] output = new String[]{"climb", "the", "mountain", "just", "a", "little",
            "bit", "to", "test", "that", "it's", "a", "mountain"};

        for (int i = 0; i < input.length; i++) {
            when(wp.processWord(input[i])).thenReturn(output[i]);
        }
    }

    /**
     * Mocks the word processor behavior for the following sentence:<br>
     * "From the top of the mountain, you cannot see the mountain."
     *
     * @param wp the {@code WordProcessor}.
     */
    private static void mockProcessorForThirdSentence(WordProcessor wp) {
        String[] input = new String[]{"From", "top", "of", "mountain,", "you", "cannot",
            "see", "the", "mountain."};

        String[] output = new String[]{"from", "top", "of", "mountain", "you", "cannot",
            "see", "the", "mountain"};

        for (int i = 0; i < input.length; i++) {
            when(wp.processWord(input[i])).thenReturn(output[i]);
        }
    }

    /**
     * Mocks the word processor behavior for the following sentence:<br>
     * "A beginning is the time for taking the most delicate care that the
     * balances are correct."
     *
     * @param wp the {@code WordProcessor}.
     */
    private static void mockProcessorForFourthSentence(WordProcessor wp) {
        String[] input = new String[]{"A", "beginning", "is", "the", "time", "for",
            "taking", "the", "most", "delicate", "care", "that",
            "the", "balances", "are", "correct."};

        String[] output = new String[]{"a", "beginning", "is", "the", "time", "for",
            "taking", "the", "most", "delicate", "care", "that",
            "the", "balances", "are", "correct"};

        for (int i = 0; i < input.length; i++) {
            when(wp.processWord(input[i])).thenReturn(output[i]);
        }
    }

    /**
     * Gets the expected words from the following sentence:<br>
     * "Any road followed precisely to its end leads precisely nowhere."
     */
    private static List<WordOccurrence> getFirstSentenceWords() {
        return Arrays.asList(
                new WordOccurrence("any", 1),
                new WordOccurrence("road", 1),
                new WordOccurrence("followed", 1),
                new WordOccurrence("precisely", 2),
                new WordOccurrence("to", 1),
                new WordOccurrence("its", 1),
                new WordOccurrence("end", 1),
                new WordOccurrence("leads", 1),
                new WordOccurrence("nowhere", 1)
        );
    }

    /**
     * Gets the expected words from the following sentence:<br>
     * "Climb the mountain just a little bit to test that it's a mountain."
     */
    private static List<WordOccurrence> getSecondSentenceWords() {
        return Arrays.asList(
                new WordOccurrence("climb", 1),
                new WordOccurrence("the", 1),
                new WordOccurrence("mountain", 2),
                new WordOccurrence("just", 1),
                new WordOccurrence("a", 2),
                new WordOccurrence("little", 1),
                new WordOccurrence("bit", 1),
                new WordOccurrence("to", 1),
                new WordOccurrence("test", 1),
                new WordOccurrence("that", 1),
                new WordOccurrence("it's", 1)
        );
    }

    /**
     * Gets the expected words from the following sentence:<br>
     * "From the top of the mountain, you cannot see the mountain."
     */
    private static List<WordOccurrence> getThirdSentenceWords() {
        return Arrays.asList(
                new WordOccurrence("from", 1),
                new WordOccurrence("the", 1),
                new WordOccurrence("top", 1),
                new WordOccurrence("of", 1),
                new WordOccurrence("the", 2),
                new WordOccurrence("mountain", 2),
                new WordOccurrence("you", 1),
                new WordOccurrence("cannot", 1),
                new WordOccurrence("see", 1)
        );
    }

    /**
     * Gets the expected words from the following sentence:<br>
     * "A beginning is the time for taking the most delicate care that the
     * balances are correct."
     */
    private static List<WordOccurrence> getFourthSentenceWords() {
        return Arrays.asList(
                new WordOccurrence("a", 1),
                new WordOccurrence("beginning", 1),
                new WordOccurrence("is", 1),
                new WordOccurrence("the", 3),
                new WordOccurrence("most", 1),
                new WordOccurrence("delicate", 1),
                new WordOccurrence("care", 1),
                new WordOccurrence("that", 1),
                new WordOccurrence("balances", 1),
                new WordOccurrence("are", 1),
                new WordOccurrence("correct", 1)
        );
    }

    /**
     * Gets the expected words from the all four sentences.
     */
    private static List<WordOccurrence> getAllSentencesWords() {
        return Arrays.asList(
                new WordOccurrence("any", 1),
                new WordOccurrence("road", 1),
                new WordOccurrence("followed", 1),
                new WordOccurrence("precisely", 2),
                new WordOccurrence("to", 2),
                new WordOccurrence("its", 1),
                new WordOccurrence("end", 1),
                new WordOccurrence("leads", 1),
                new WordOccurrence("nowhere", 1),
                new WordOccurrence("climb", 1),
                new WordOccurrence("the", 7),
                new WordOccurrence("mountain", 4),
                new WordOccurrence("just", 1),
                new WordOccurrence("a", 3),
                new WordOccurrence("little", 1),
                new WordOccurrence("bit", 1),
                new WordOccurrence("test", 1),
                new WordOccurrence("that", 2),
                new WordOccurrence("it's", 1),
                new WordOccurrence("from", 1),
                new WordOccurrence("top", 1),
                new WordOccurrence("of", 1),
                new WordOccurrence("you", 1),
                new WordOccurrence("cannot", 1),
                new WordOccurrence("see", 1),
                new WordOccurrence("beginning", 1),
                new WordOccurrence("is", 1),
                new WordOccurrence("time", 1),
                new WordOccurrence("for", 1),
                new WordOccurrence("taking", 1),
                new WordOccurrence("most", 1),
                new WordOccurrence("delicate", 1),
                new WordOccurrence("care", 1),
                new WordOccurrence("balances", 1),
                new WordOccurrence("are", 1),
                new WordOccurrence("correct", 1)
        );
    }
}
