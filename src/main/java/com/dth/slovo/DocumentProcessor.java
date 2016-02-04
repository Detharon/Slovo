package com.dth.slovo;

import com.dth.entity.WordOccurrence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentProcessor {
    private final File file;
    private final ArrayList<WordOccurrence> words = new ArrayList<>(100);
    
    /**
     * Reads a text file and processes its content, by fetching words and
     * counting their occurrences.
     * @param file A file to be processed.
     */
    public DocumentProcessor(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
    
    public ArrayList<WordOccurrence> getWords() {
        return words;
    }
    
    /**
     * Processes the text file and populates its internal list of words.
     */
    public void processFile() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] tempWords = line.split(" ");
                WordProcessor wp = new WordProcessor();
                
                for (String temp : tempWords) {
                    temp = wp.processWord(temp);
                    if (temp == null) continue;
                    
                    WordOccurrence wordOccurence = new WordOccurrence(temp);

                    Optional<WordOccurrence> sameWord = words.parallelStream()
                            .filter(w -> w.getWord().equals(wordOccurence.getWord()))
                            .findAny();
                    
                    if (sameWord.isPresent()) {
                        sameWord.get().incrementCount();
                    } else {
                        words.add(wordOccurence);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
