package com.dth.slovo;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WordProcessorTest {
    private final WordProcessor wp = new WordProcessor();

    @Test
    public void noChanges() {
        String word = "word";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void toLowerCase() {
        String word = "WoRd";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void toLowerCaseRussian() {
        String word = "СЛоВо";
        String expResult = "слово";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void removeLeadingComa() {
        String word = ",word";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void removeWhitespaces() {
        String word = "  word   ";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void removeTrailingDot() {
        String word = "word.";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void removeLeadingTrailingUnderscore() {
        String word = "_word_";
        String expResult = "word";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void removeLeadingTrailingQuestionMark_toLowerCaseSpanish() {
        String word = "¿QUÉ?";
        String expResult = "qué";
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void onlyDot() {
        String word = ".";
        String expResult = null;
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void emptyString() {
        String word = "";
        String expResult = null;
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void spaceTab() {
        String word = "     ";
        String expResult = null;
        String result = wp.processWord(word);
        
        assertEquals(expResult, result);
    }
}
