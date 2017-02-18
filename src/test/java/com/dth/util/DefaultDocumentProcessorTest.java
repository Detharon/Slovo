package com.dth.util;

import com.dth.entity.Sentence;
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
import org.junit.BeforeClass;

public class DefaultDocumentProcessorTest {

    private static String FILENAME;
    private static File fileWithText;
    private static File fileWithResults;

    @BeforeClass
    public static void setUp() throws URISyntaxException {
        FILENAME = "English";
        System.out.println("Filename = "+FILENAME);
        System.out.println(DefaultDocumentProcessorTest.class.getClassLoader().getResource(FILENAME + ".txt").toURI());

        fileWithText = new File(DefaultDocumentProcessorTest.class.getClassLoader().getResource(FILENAME + ".txt").toURI());
        fileWithResults = new File(DefaultDocumentProcessorTest.class.getClassLoader().getResource(FILENAME + "Sentences.txt").toURI());
    }

    /**
     * Checks whether a block of text is properly separated into sentences.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void processEnglish() throws IOException, URISyntaxException {
        DefaultDocumentProcessor documentProcessor
                = new DefaultDocumentProcessor();
        documentProcessor.processDocument(fileWithText);

        Set<Sentence> resultSentences = new HashSet<>(documentProcessor.getSentences());
        Set<Sentence> expectedSentences = getExpectedSentences();

        assertThat(expectedSentences, is(resultSentences));
    }

    /**
     * A helper method that reads a text file and creates sentences from it.
     * Each line is a separate {@code Sentence}.
     *
     * @return the sentences.
     */
    private Set<Sentence> getExpectedSentences() {
        HashSet<Sentence> expectedSentences = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileWithResults))) {

            String line;
            while ((line = br.readLine()) != null) {
                expectedSentences.add(new Sentence(line));
            }
        } catch (IOException ex) {
            Logger.getLogger(DefaultDocumentProcessorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return expectedSentences;
    }
}
