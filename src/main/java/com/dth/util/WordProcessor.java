package com.dth.util;

/**
 * WordProcessor represents an object that transforms the word,
 * by removing redundant characters or case differences.
 * 
 * <p>For example {@code "well, "}, {@code "well.}, and {@code "Well"} are
 * all different strings, but in fact they represent the same word.
 * 
 * <p>The purpose of {@code WordProcessor} is to clean and standardize
 * the word representation.
 *
 * @param <T>   the type of the word representation.
 */
public interface WordProcessor<T> {

    /**
     * Performs the necessary transformations to standardize the word
     * representation.
     * 
     * @param word  the word to be processed.
     * @return      the resultant word.
     */
    public T processWord(T word);
}
