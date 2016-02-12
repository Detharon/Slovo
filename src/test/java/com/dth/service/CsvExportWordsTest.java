package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class CsvExportWordsTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private CsvExportWords export;
    private Writer writer;
    
    @Before
    public void setUp() {
        // Dummy writer object
        writer = mock(Writer.class);
        export = new CsvExportWords(writer);
    }

    @Test
    public void writeAndClose() {
        export.export(getTestWords(), 1000);
        export.close();
        
        try {
            verify(writer).write("word,3"+LINE_SEPARATOR);
            verify(writer).write("слово,2"+LINE_SEPARATOR);
            verify(writer).write("sucedió,1"+LINE_SEPARATOR);
            verify(writer).close();
        } catch (IOException ex) {
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
