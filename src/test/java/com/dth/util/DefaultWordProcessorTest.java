package com.dth.util;

import com.dth.util.DefaultWordProcessor;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DefaultWordProcessorTest {
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
            {"", "."},
            {"", ""},
            {"", "    "}            
        });
    }
    
    public DefaultWordProcessorTest(String expected, String result) {
        this.expected = expected;
        this.result = result;
    }
    
    @Test
    public void testProcessWord() {
        DefaultWordProcessor wp = new DefaultWordProcessor();
        assertEquals(expected, wp.processWord(result));
    }
}
