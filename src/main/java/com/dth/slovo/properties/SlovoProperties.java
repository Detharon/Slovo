package com.dth.slovo.properties;

import java.util.Properties;

/**
 * Just like the {@link Properties} class, SlovoProperties represents a
 * persistent settings in a form of key-value pairs.
 *
 * It was tailored for the needs of Slovo application and it contains a number
 * of static strings that facilitate the process of getting and setting specific
 * key values.
 *
 * Its default constructor ensures that all expected fields are initialized with
 * default values.
 */
public class SlovoProperties extends Properties {

    public static final String NUMBER_OF_WORDS = "numberOfWords";
    private static final String DEFAULT_NUMBER_OF_WORDS = "1000";

    /**
     * Creates a property list that already contain all the expected keys with
     * their default values.
     */
    public SlovoProperties() {
        defaults = generateDefaults();
    }

    // --------------------------------------------------
    // Getters / setters
    // --------------------------------------------------
    /**
     * Returns the number of words to be displayed.
     *
     * @return the number of words.
     */
    public int getNumberOfWords() {
        return Integer.parseInt(getProperty(NUMBER_OF_WORDS));
    }

    /**
     * Sets the number of words to be displayed.
     *
     * @param numberOfWords the number of words.
     */
    public void setNumberOfWords(int numberOfWords) {
        setProperty(SlovoProperties.NUMBER_OF_WORDS, String.valueOf(numberOfWords));
    }
    
    /**
     * Sets the number of words to be displayed.
     *
     * @param numberOfWords the number of words.
     */
    public void setNumberOfWords(String numberOfWords) {
        setProperty(SlovoProperties.NUMBER_OF_WORDS, numberOfWords);
    }
    
    /**
     * Returns the default number of words to be displayed.
     * 
     * @return the default number of words.
     */
    public int getDefaultNumberOfWords() {
        return Integer.parseInt(DEFAULT_NUMBER_OF_WORDS);
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------
    /**
     * Creates the default properties object, that will containing all available
     * property keys with their respective default values.
     *
     * @return the default properties.
     */
    private Properties generateDefaults() {
        Properties newDefaults = new Properties();
        newDefaults.setProperty(NUMBER_OF_WORDS, DEFAULT_NUMBER_OF_WORDS);
        return newDefaults;
    }
}
