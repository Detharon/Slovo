package com.dth.util;

import java.util.Collection;

/**
 * DocumentProcessor is responsible for parsing a file using the
 * {@code #processFile()} method and generating a list of results,
 * which can be accessed from the {@code #getWords()} method.
 * 
 * @param <T>   the class that represents the words.
 * 
 * @see {@code WordOccurrence}
 */
public interface DocumentProcessor<T> {

    /**
     * Reads the file and processes its contents. The resultant words
     * should be stored in an internal collection.
     * 
     * NOTE: Because the results often need to be preprocessed 
     * before they can be accessed, it's advised to use an utility
     * class that does this task, a {@code WordProcessor} for example.
     */
    public void processFile();

    /**
     * Returns the collection of words generated by the
     * {@code #processFile()} method. 
     * 
     * @return  the collection of processed words.
     */
    public Collection<T> getWords();
}
