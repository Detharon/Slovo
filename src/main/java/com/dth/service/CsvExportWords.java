package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import javax.persistence.EntityManager;

public class CsvExportWords implements ExportWords {
    private final static String FORMAT = "%s,%d%n";
    
    private EntityManager em;
    private FetchWords fw;
    
    public CsvExportWords(EntityManager em, FetchWords fw) {
        this.em = em;
        this.fw = fw;
    }
    
    @Override
    public void export(File file) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            fw = new FetchWords(em);
            List<WordOccurrence> words = fw.execute(1000);
            
            for (WordOccurrence w : words) {
                bw.write(String.format(FORMAT, w.getWord(), w.getId()));
            }
        } catch (IOException ex) {
            // TODO handle this exception
        }
    }
}
