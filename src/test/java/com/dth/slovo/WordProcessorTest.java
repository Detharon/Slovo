package com.dth.slovo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WordProcessorTest {
    private final String expected;
    private final String result;

    @Parameters
    public static Collection<String[]> getTestParameters() {
        return Arrays.asList(new String[][]{
            {"word", "word"},
            {"word", "WoRd"},
            {"слово", "сЛоВо"},
            {"word", ",word"},
            {"word", "  word  "},
            {"word", "word."},
            {"word", "_word_"},
            {"qué", "¿QUÉ?"},
            {null, "."},
            {null, ""},
            {null, "    "}            
        });
    }
    
    public WordProcessorTest(String expected, String result) {
        this.expected = expected;
        this.result = result;
    }
    
    @Test
    public void testProcessWord() {
        WordProcessor wp = new WordProcessor();
        assertEquals(expected, wp.processWord(result));
    }
}
