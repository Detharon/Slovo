package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvImportWords implements ImportWords {

    private final BufferedReader reader;
    private final String delimiter;

    public CsvImportWords(BufferedReader reader) {
        this(reader, ",");
    }

    public CsvImportWords(BufferedReader reader, String delimiter) {
        this.reader = reader;
        this.delimiter = delimiter;
    }
    
    @Override
    public List<WordOccurrence> importWords() throws TransferFailedException {
        List<WordOccurrence> words = new ArrayList<>(1000);
        
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter);
                words.add(new WordOccurrence(values[0], Integer.parseInt(values[1])));
            }            
        } catch (IOException ex) {
            throw new TransferFailedException(ex);
        }
        
        return words;
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
