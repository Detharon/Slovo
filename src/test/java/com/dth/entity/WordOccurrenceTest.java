/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dth.entity;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Piotr
 */
public class WordOccurrenceTest {
    
    // --------------------------------------------------
    // Testing equals
    // --------------------------------------------------
    
    @Test
    public void differentWord() {
        WordOccurrence first = new WordOccurrence("word");
        WordOccurrence second = new WordOccurrence("nord");

        assertNotEquals(first, second);
    }
    
    @Test
    public void sameWord() {
        WordOccurrence first = new WordOccurrence("word");
        WordOccurrence second = new WordOccurrence("word");

        assertEquals(first, second);
    }
    
    @Test
    public void sameWord_differentCount() {
        WordOccurrence first = new WordOccurrence("word", 1);
        WordOccurrence second = new WordOccurrence("word", 2);

        assertNotEquals(first, second);
    }
    
    @Test
    public void differentWord_sameCount() {
        WordOccurrence first = new WordOccurrence("word", 1);
        WordOccurrence second = new WordOccurrence("nord", 1);

        assertNotEquals(first, second);
    }
    
    // --------------------------------------------------
    // Testing compareTo
    // --------------------------------------------------
    
    @Test
    public void sameNames() {
        WordOccurrence first = new WordOccurrence("word");
        WordOccurrence second = new WordOccurrence("word");
        
        assertTrue(first.compareTo(second) == 0);
    }
    
    @Test
    public void sameNamesGreaterCount() {
        WordOccurrence first = new WordOccurrence("word", 5);
        WordOccurrence second = new WordOccurrence("word", 3);
        
        assertTrue(first.compareTo(second) == 2);
    }
    
    @Test
    public void sameNamesLesserCount() {
        WordOccurrence first = new WordOccurrence("word", 3);
        WordOccurrence second = new WordOccurrence("word", 10);
        
        assertTrue(first.compareTo(second) == -7);
    }
    
    @Test
    public void differentNamesGreater() {
        WordOccurrence first = new WordOccurrence("word");
        WordOccurrence second = new WordOccurrence("nord");

        assertTrue(first.compareTo(second) > 0);
    }
    
    @Test
    public void differentNamesLesser() {
        WordOccurrence first = new WordOccurrence("ace");
        WordOccurrence second = new WordOccurrence("beetle");
        
        assertTrue(first.compareTo(second) < 0);
    }
    
    @Test
    public void differentNamesDifferentCount() {
        WordOccurrence first = new WordOccurrence("ace", 4);
        WordOccurrence second = new WordOccurrence("beetle", 14);
        
        assertTrue(first.compareTo(second) == -10);
    }
}
