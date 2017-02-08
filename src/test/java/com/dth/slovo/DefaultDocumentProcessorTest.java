package com.dth.slovo;

import com.dth.entity.Sentence;
import com.dth.util.DefaultDocumentProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class DefaultDocumentProcessorTest {

    private static final String FILENAME = "English";
    private static File file = null;

    @Test
    public void processEnglish() {
        DefaultDocumentProcessor documentProcessor
                = new DefaultDocumentProcessor();
        documentProcessor.processDocument(file);

        Set<Sentence> resultSentences = new HashSet<>(documentProcessor.getSentences());
        Set<Sentence> expectedSentences = getExpectedSentences();

        assertThat(expectedSentences, is(resultSentences));
    }

    private Set<Sentence> getExpectedSentences() {
        HashSet<Sentence> expectedSentences = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(DefaultDocumentProcessorTest.class.getClassLoader().getResource(FILENAME + "Sentences.txt").toURI())))) {

            String line;
            while ((line = br.readLine()) != null) {
                expectedSentences.add(new Sentence(line));
            }
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(DefaultDocumentProcessorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return expectedSentences;
    }
}
