package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class CsvExportWordsTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private CsvExportWords export;
    private Writer writer;
    
    @Before
    public void setUp() {
        // FetchWords mock
        FetchWords fetchWords = mock(FetchWords.class);
        when(fetchWords.execute(anyInt())).thenReturn(
                Arrays.asList(new WordOccurrence[]{
                    new WordOccurrence("word", 3),
                    new WordOccurrence("слово", 2),
                    new WordOccurrence("sucedió", 1),
                })
        );
        
        // Dummy writer object
        writer = mock(Writer.class);
        
        try {
            export = new CsvExportWords(fetchWords, writer);
            
            writer = mock(Writer.class);
            export.setWriter(writer);
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {}
    }

    @Test
    public void writeAndClose() {
        export.export(1000);
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
    
}
