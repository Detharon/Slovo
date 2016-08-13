package com.dth.slovo.properties;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SlovoPropertiesTest {

    private static SlovoProperties properties;

    @Before
    public void setUp() {
        properties = new SlovoProperties();
    }

    /**
     * Test of getDefaultNumberOfWords method, of class SlovoProperties.
     *
     * Checks whether the default number of words is equal to 1000.
     */
    @Test
    public void testGetDefaultNumberOfWords() {
        int expected = properties.getDefaultNumberOfWords();
        int result = 1000;

        assertEquals(expected, result);
    }

    /**
     * Test of getNumberOfWords method, of class SlovoProperties.
     *
     * Checks whether the default value returned by getNumberOfWords is equal to
     * 1000.
     */
    @Test
    public void testGetNumberOfWordsDef() {
        int expected = 1000;
        int result = properties.getNumberOfWords();
        assertEquals(expected, result);
    }

    /**
     * Test of getNumberOfWords method, of class SlovoProperties.
     *
     * Checks whether the value returned by getNumberOfWords is equal to what
     * was previously set by setNumberOfWords.
     */
    @Test
    public void testGetNumberOfWords() {
        int expected = 2000;
        properties.setNumberOfWords(expected);
        int result = properties.getNumberOfWords();
        assertEquals(expected, result);
    }
}
