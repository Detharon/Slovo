package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

public class CsvExportWords implements ExportWords {
    private final static String FORMAT = "%s,%d%n";
    private final Writer writer;

    public CsvExportWords(Writer writer) throws UnsupportedEncodingException, FileNotFoundException {
        this.writer = writer;
    }
    
    @Override
    public void export(List<WordOccurrence> words, int count) {
        for (WordOccurrence w : words) {
            try {
                writer.write(String.format(FORMAT, w.getWord(), w.getCount()));
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
    
    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
}
