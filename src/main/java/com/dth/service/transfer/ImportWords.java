package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.util.List;

/**
 * An {@code ImportWords} object, responsible for importing the words.
 */
public interface ImportWords {

    /**
     * Imports words and returns the list of newly created {@code WordOccurrence)
     * instances.
     *
     * @return the list of words.
     *
     * @throws TransferFailedException
     */
    public List<WordOccurrence> importWords() throws TransferFailedException;

    /**
     * Closes the opened resources which were necessary for import.
     *
     * @throws IOException
     */
    public void close() throws IOException;
}
