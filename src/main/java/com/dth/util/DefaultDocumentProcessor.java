package com.dth.util;

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

public class DefaultDocumentProcessor<T> implements DocumentProcessor {

    private final File file;
    private final WordProcessor processor;
    private final ArrayList<WordOccurrence> words = new ArrayList<>(100);

    /**
     * Reads a text file and processes its content, by fetching words and
     * counting their occurrences.
     *
     * @param file the file to be processed.
     * @param processor the word processor used to transform the word.
     */
    public DefaultDocumentProcessor(File file, WordProcessor processor) {
        this.file = file;
        this.processor = processor;
    }
    
    public File getFile() {
        return file;
    }

    @Override
    public ArrayList<WordOccurrence> getWords() {
        return words;
    }

    /**
     * Processes the text file and populates its internal list of words.
     */
    @Override
    public void processFile() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF8"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] tempWords = line.split(" ");
                
                for (Object temp : tempWords) {
                    temp = processor.processWord(temp);
                    if (temp == null) {
                        continue;
                    }

                    WordOccurrence wordOccurence = new WordOccurrence(temp.toString());

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
            Logger.getLogger(DefaultDocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
