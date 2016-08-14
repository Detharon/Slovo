package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvExportWords implements ExportWords {

    private final OutputStreamWriter writer;
    private final String delimiter;

    public CsvExportWords(OutputStreamWriter writer) {
        this(writer, ",");
    }

    public CsvExportWords(OutputStreamWriter writer, String delimiter) {
        this.writer = writer;
        this.delimiter = "%s" + delimiter + "%d%n";
    }

    @Override
    public void export(List<WordOccurrence> words) throws ExportFailedException {
        for (WordOccurrence w : words) {
            try {
                writer.write(String.format(delimiter, w.getWord(), w.getCount()));
            } catch (IOException ex) {
                throw new ExportFailedException(ex);
            }
        }
    }
}
