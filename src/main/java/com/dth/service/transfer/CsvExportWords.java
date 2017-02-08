package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvExportWords implements ExportWords {

    private final OutputStreamWriter writer;
    private final String format;

    public CsvExportWords(OutputStreamWriter writer) {
        this(writer, ",");
    }

    public CsvExportWords(OutputStreamWriter writer, String delimiter) {
        this.writer = writer;
        this.format = "%s" + delimiter + "%d%n";
    }

    @Override
    public void exportWords(List<WordOccurrence> words) throws TransferFailedException {
        for (WordOccurrence w : words) {
            try {
                writer.write(String.format(format, w.getWord(), w.getCount()));
            } catch (IOException ex) {
                throw new TransferFailedException(ex);
            }
        }
    }
}
