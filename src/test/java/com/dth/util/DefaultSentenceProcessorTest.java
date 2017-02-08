package com.dth.util;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSentenceProcessorTest {
    
    private static DefaultSentenceProcessor processor;
    
    @BeforeClass
    public static void setUp() throws URISyntaxException {
        WordProcessor<String> wp = mock(WordProcessor.class);
        
        when(wp.processWord("Any")).thenReturn("any");
        when(wp.processWord("road")).thenReturn("road");
        when(wp.processWord("followed")).thenReturn("followed");
        when(wp.processWord("precisely")).thenReturn("precisely");
        when(wp.processWord("to")).thenReturn("to");
        when(wp.processWord("its")).thenReturn("its");
        when(wp.processWord("end")).thenReturn("end");
        when(wp.processWord("leads")).thenReturn("leads");
        when(wp.processWord("precisely")).thenReturn("precisely");
        when(wp.processWord("nowhere.")).thenReturn("nowhere");
        
        when(wp.processWord("Climb")).thenReturn("climb");
        when(wp.processWord("the")).thenReturn("the");
        when(wp.processWord("mountain")).thenReturn("mountain");
        when(wp.processWord("just")).thenReturn("just");
        when(wp.processWord("a")).thenReturn("a");
        when(wp.processWord("little")).thenReturn("little");
        when(wp.processWord("bit")).thenReturn("bit");
        when(wp.processWord("to")).thenReturn("to");
        when(wp.processWord("test")).thenReturn("test");
        when(wp.processWord("that")).thenReturn("that");
        when(wp.processWord("it's")).thenReturn("it's");
        when(wp.processWord("mountain.")).thenReturn("mountain");
        
        when(wp.processWord("From")).thenReturn("from");
        when(wp.processWord("top")).thenReturn("top");
        when(wp.processWord("of")).thenReturn("of");
        when(wp.processWord("mountain,")).thenReturn("mountain");
        when(wp.processWord("you")).thenReturn("you");
        when(wp.processWord("cannot")).thenReturn("cannot");
        when(wp.processWord("see")).thenReturn("see");
        
        processor = new DefaultSentenceProcessor(wp);
    }

    /**
     * Test of processSentence method, of class DefaultSentenceProcessor.
     */
    @Test
    public void testProcessSentence() {
        processor.processSentence(new Sentence("Any road followed precisely to its end leads precisely nowhere."));
        processor.processSentence(new Sentence("Climb the mountain just a little bit to test that it's a mountain."));
        processor.processSentence(new Sentence("From the top of the mountain, you cannot see the mountain."));
        Set expectedWords = getExpectedWords();
        Set resultWords = new HashSet(processor.getWords());

        assertThat(expectedWords, is(resultWords));
    }
    
    private Set<WordOccurrence> getExpectedWords() {
        Set<WordOccurrence> expectedWords = new HashSet<>();
        expectedWords.add(new WordOccurrence("any", 1));
        expectedWords.add(new WordOccurrence("road", 1));
        expectedWords.add(new WordOccurrence("followed", 1));
        expectedWords.add(new WordOccurrence("precisely", 2));
        expectedWords.add(new WordOccurrence("to", 2));
        expectedWords.add(new WordOccurrence("its", 1));
        expectedWords.add(new WordOccurrence("end", 1));
        expectedWords.add(new WordOccurrence("leads", 1));
        expectedWords.add(new WordOccurrence("nowhere", 1));
        expectedWords.add(new WordOccurrence("climb", 1));
        expectedWords.add(new WordOccurrence("the", 4));
        expectedWords.add(new WordOccurrence("mountain", 4));
        expectedWords.add(new WordOccurrence("just", 1));
        expectedWords.add(new WordOccurrence("a", 2));
        expectedWords.add(new WordOccurrence("little", 1));
        expectedWords.add(new WordOccurrence("bit", 1));
        expectedWords.add(new WordOccurrence("test", 1));
        expectedWords.add(new WordOccurrence("that", 1));
        expectedWords.add(new WordOccurrence("it's", 1));
        expectedWords.add(new WordOccurrence("from", 1));
        expectedWords.add(new WordOccurrence("top", 1));
        expectedWords.add(new WordOccurrence("of", 1));
        expectedWords.add(new WordOccurrence("you", 1));
        expectedWords.add(new WordOccurrence("cannot", 1));
        expectedWords.add(new WordOccurrence("see", 1));
        return expectedWords;
    }
}

