package com.dth.slovo;

import com.dth.entity.WordOccurrence;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class DocumentProcessorTest {
    private static final String ENGLISH_SIMPLE = "EnglishSimple.txt";
    
    @Test
    public void processEnglishSimple() {
        File file = null;
        try {
            file = new File(getClass().getClassLoader().getResource(ENGLISH_SIMPLE).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace(System.out);
        }

        if (file == null) fail("File not found.");
        
        DocumentProcessor documentProcessor = new DocumentProcessor(file);        
        documentProcessor.processFile();
        ArrayList<WordOccurrence> resultWords = new ArrayList<>(documentProcessor.getWords());
        
        ArrayList<WordOccurrence> expectedWords = new ArrayList<>();
        expectedWords.add(new WordOccurrence("i", 4));
        expectedWords.add(new WordOccurrence("will", 4));
        expectedWords.add(new WordOccurrence("fear", 4));
        expectedWords.add(new WordOccurrence("the", 2));
        expectedWords.add(new WordOccurrence("where", 1));
        expectedWords.add(new WordOccurrence("be", 1));
        expectedWords.add(new WordOccurrence("has", 1));
        expectedWords.add(new WordOccurrence("gone", 1));
        expectedWords.add(new WordOccurrence("there", 1));
        expectedWords.add(new WordOccurrence("shall", 1));
        expectedWords.add(new WordOccurrence("nothing", 1));
        expectedWords.add(new WordOccurrence("only", 1));
        expectedWords.add(new WordOccurrence("remain", 1));
        expectedWords.add(new WordOccurrence("me", 1));
        expectedWords.add(new WordOccurrence("through", 1));
        expectedWords.add(new WordOccurrence("pass", 1));
        expectedWords.add(new WordOccurrence("it", 1));
        expectedWords.add(new WordOccurrence("let", 1));
        expectedWords.add(new WordOccurrence("my", 1));
        expectedWords.add(new WordOccurrence("face", 1));
        expectedWords.add(new WordOccurrence("mind-killer", 1));
        expectedWords.add(new WordOccurrence("is", 1));        
        expectedWords.add(new WordOccurrence("not", 1));
        
        Collections.sort(resultWords, Collections.reverseOrder());
        Collections.sort(expectedWords, Collections.reverseOrder());
        
        System.out.println("Testing DocumentProcessor on"+ENGLISH_SIMPLE);
        System.out.println("Expected | Result"+ENGLISH_SIMPLE);
        
        for (int i = 0; i < expectedWords.size(); i++) {
            System.out.println(expectedWords.get(i).toString() + " | " + resultWords.get(i).toString());
        }
        
        assertTrue(CollectionUtils.isEqualCollection(expectedWords, resultWords));
    }

    @Test
    public void testSaveToDatabase() {
        assertTrue("TODO database", true);
    }
}
