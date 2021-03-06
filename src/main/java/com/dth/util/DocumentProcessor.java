package com.dth.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * DocumentProcessor is responsible for parsing a file using the
 * {@code #processFile()} method and generating a list of results, which can be
 * accessed from the {@code #getWords()} method.
 *
 * @param <T> the class that represents the words.
 *
 * @see {@code WordOccurrence}
 */
public interface DocumentProcessor<T> {

    /**
     * Reads the file and processes its contents. The resultant sentences will
     * be stored in an internal collection, which can be accessed from
     * {@code getSentences()} method.
     *
     * @param file the file to be processed.
     * @throws java.io.IOException
     */
    public void processDocument(File file) throws IOException;

    /**
     * Returns the collection of sentences generated by the
     * {@code #processDocument()} method.
     *
     * @return the collection of sentences.
     */
    public Collection<T> getSentences();
}
