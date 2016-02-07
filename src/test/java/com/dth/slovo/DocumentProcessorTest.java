package com.dth.slovo;

import com.dth.entity.WordOccurrence;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

public class DocumentProcessorTest {
    private static final String ENGLISH_SIMPLE = "EnglishSimple.txt";
    private static File file = null;
    
    @BeforeClass
    public static void setUp() throws URISyntaxException {
        file = new File(DocumentProcessorTest.class.getClassLoader().getResource(ENGLISH_SIMPLE).toURI());
    }
    
    @Test
    public void processEnglishSimple() {
        DocumentProcessor documentProcessor = new DocumentProcessor(file);        
        documentProcessor.processFile();
        
        ArrayList<WordOccurrence> resultWords = new ArrayList<>(documentProcessor.getWords());        
        ArrayList<WordOccurrence> expectedWords = getExpectedWords();
        
        Collections.sort(resultWords, Collections.reverseOrder());
        Collections.sort(expectedWords, Collections.reverseOrder());
        
        System.out.println("Testing DocumentProcessor on"+ENGLISH_SIMPLE);
        System.out.println("Expected | Result"+ENGLISH_SIMPLE);
        
        for (int i = 0; i < expectedWords.size(); i++) {
            System.out.println(expectedWords.get(i).toString() + " | " + resultWords.get(i).toString());
        }
        
        assertTrue(CollectionUtils.isEqualCollection(expectedWords, resultWords));
    }
    
    private ArrayList<WordOccurrence> getExpectedWords() {
        ArrayList<WordOccurrence> expectedWords = new ArrayList<>();
        
        expectedWords.addAll(Arrays.asList(new WordOccurrence[]{
            new WordOccurrence("i", 4),
            new WordOccurrence("will", 4),
            new WordOccurrence("fear", 4),
            new WordOccurrence("the", 2),
            new WordOccurrence("where", 1),
            new WordOccurrence("be", 1),
            new WordOccurrence("has", 1),
            new WordOccurrence("gone", 1),
            new WordOccurrence("there", 1),
            new WordOccurrence("shall", 1),
            new WordOccurrence("nothing", 1),
            new WordOccurrence("only", 1),
            new WordOccurrence("remain", 1),
            new WordOccurrence("me", 1),
            new WordOccurrence("through", 1),
            new WordOccurrence("pass", 1),
            new WordOccurrence("it", 1),
            new WordOccurrence("let", 1),
            new WordOccurrence("my", 1),
            new WordOccurrence("face", 1),
            new WordOccurrence("mind-killer", 1),
            new WordOccurrence("is", 1),
            new WordOccurrence("not", 1)
        }));
        
        return expectedWords;
    }
}

