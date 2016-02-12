package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

public class CsvExportWords implements ExportWords {
    private final static String FORMAT = "%s,%d%n";
    private FetchWords fw;
    private Writer writer;

    public CsvExportWords(FetchWords fw, Writer writer) throws UnsupportedEncodingException, FileNotFoundException {
        this.fw = fw;
        this.writer = writer;
    }
    
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
    @Override
    public void export(int count) {
        List<WordOccurrence> words = fw.execute(count);

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
