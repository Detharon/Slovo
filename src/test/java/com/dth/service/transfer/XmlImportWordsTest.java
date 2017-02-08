package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.util.List;
import javax.xml.stream.XMLStreamReader;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class XmlImportWordsTest {
    
    XmlImportWords importWords;
    XMLStreamReader reader;
    
    @Before
    public void setUp() {
        // Dummy writer object
        reader = mock(XMLStreamReader.class);
        importWords = new XmlImportWords(reader);
    }

    /**
     * Test of importWords method, of class XmlImportWords.
     */
    @Test
    public void testImportWords() throws Exception {
        List<WordOccurrence> result = importWords.importWords();
    }
    
    
}
