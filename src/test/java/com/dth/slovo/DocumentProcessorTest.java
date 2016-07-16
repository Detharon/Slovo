package com.dth.slovo;

import com.dth.util.DefaultDocumentProcessor;
import com.dth.entity.WordOccurrence;
import com.dth.util.DefaultWordProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.hamcrest.CoreMatchers.is;

public class DocumentProcessorTest {
    private static final String ENGLISH_SIMPLE = "EnglishSimple";
    private static File file = null;
    
    @BeforeClass
    public static void setUp() throws URISyntaxException {
        file = new File(DocumentProcessorTest.class.getClassLoader().getResource(ENGLISH_SIMPLE+".txt").toURI());
    }
    
    @Test
    public void processEnglishSimple() {
        DefaultDocumentProcessor documentProcessor =
                new DefaultDocumentProcessor(file, new DefaultWordProcessor());        
        documentProcessor.processFile();
        
        Set<WordOccurrence> resultWords = new HashSet<>(documentProcessor.getWords());        
        Set<WordOccurrence> expectedWords = getExpectedWords();
        
        assertThat(expectedWords, is(resultWords));
    }
    
    private Set<WordOccurrence> getExpectedWords() {
        HashSet<WordOccurrence> expectedWords = new HashSet<>();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(
                    new File(DocumentProcessorTest.class.getClassLoader()
                            .getResource(ENGLISH_SIMPLE + ".properties").toURI())));
        } catch (IOException | URISyntaxException ex) {
            System.out.println("Could not load+"+ENGLISH_SIMPLE+"+properties file");
        }

        prop.forEach((k, v) -> {
            expectedWords.add(new WordOccurrence((String)k, Integer.valueOf((String)v)));
        });

        return expectedWords;
    }
}

