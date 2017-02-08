package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class CsvImportWordsTest {

    private CsvImportWords importWords;
    private BufferedReader reader;

    @Before
    public void setUp() throws IOException {
        reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn(
                "word,3", "слово,2", "sucedió,1", null
        );
        importWords = new CsvImportWords(reader);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of importWords method, of class CsvImportWords.
     */
    @Test
    public void testImportWords() {
        try {
            List<WordOccurrence> result = importWords.importWords();
            List<WordOccurrence> expected = getExpectedResult();

            assertEquals(expected, result);
        } catch (TransferFailedException ex) {
            fail("TransferFailedException");
        }
    }

    private List<WordOccurrence> getExpectedResult() {
        return Arrays.asList(
                new WordOccurrence("word", 3),
                new WordOccurrence("слово", 2),
                new WordOccurrence("sucedió", 1)
        );
    }
}
