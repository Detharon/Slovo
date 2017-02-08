package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class CsvExportWordsTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private CsvExportWords export;
    private OutputStreamWriter writer;
    
    @Before
    public void setUp() {
        // Dummy writer object
        writer = mock(OutputStreamWriter.class);
        export = new CsvExportWords(writer);
    }

    @Test
    public void writeAndClose() {
        try {
            export.exportWords(getTestWords());
            verify(writer).write("word,3"+LINE_SEPARATOR);
            verify(writer).write("слово,2"+LINE_SEPARATOR);
            verify(writer).write("sucedió,1"+LINE_SEPARATOR);
        } catch (TransferFailedException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    private List getTestWords() {
        return Arrays.asList(new WordOccurrence[]{
            new WordOccurrence("word", 3),
            new WordOccurrence("слово", 2),
            new WordOccurrence("sucedió", 1),});
    } 
}
