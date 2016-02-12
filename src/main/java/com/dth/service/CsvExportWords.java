package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvExportWords implements ExportWords {
    private final String FORMAT;
    private final Writer writer;
    
    public CsvExportWords(Writer writer) {
        this(writer, ",");
    }

    public CsvExportWords(Writer writer, String delimiter) {
        this.writer = writer;
        FORMAT = "%s"+delimiter+"%d%n";
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
