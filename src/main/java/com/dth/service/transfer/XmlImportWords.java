package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XmlImportWords implements ImportWords {
    
    private final XMLStreamReader streamReader;
    
    public XmlImportWords(XMLStreamReader streamReader) {
        this.streamReader = streamReader;
    }

    @Override
    @SuppressWarnings("null")
    public List<WordOccurrence> importWords() throws TransferFailedException {
        List<WordOccurrence> words = new ArrayList<>(1000);
        WordOccurrence word = null;
        
        try {
            streamReader.nextTag(); // Moves to "words" element
            
            while (streamReader.hasNext()) {
                if (streamReader.isStartElement()) {
                    switch (streamReader.getLocalName()) {
                        case "wordOccurrence":
                            word = new WordOccurrence();
                            break;
                        case "word":
                            word.setWord(streamReader.getElementText());
                            break;
                        case "count":
                            word.setCount(Integer.parseInt(streamReader.getElementText()));
                            break;
                    }
                }
                
                if (streamReader.isEndElement() && streamReader.getLocalName().equals("wordOccurrence")) {
                    words.add(word);
                }
                
                streamReader.next();
            }
        
        } catch (XMLStreamException ex) {
            throw new TransferFailedException();
        }
        
        return words;
    }
    
    @Override
    public void close() {
        // There's nothing to close
    }
}
