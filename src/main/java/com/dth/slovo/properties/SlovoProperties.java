package com.dth.slovo.properties;

import java.io.IOException;
import java.io.OutputStream;
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
    protected static final String DEFAULT_NUMBER_OF_WORDS = "1000";

    public static final String WINDOW_WIDTH = "windowWidth";
    protected static final String DEFAULT_WINDOW_WIDTH = "800";

    public static final String WINDOW_HEIGHT = "windowHeight";
    protected static final String DEFAULT_WINDOW_HEIGHT = "600";

    public static final String MAXIMIZED_MODE = "maximized";
    protected static final String DEFAULT_MAXIMIZED_MODE = "false";

    /**
     * Creates a property list that already contain all the expected keys with
     * their default values.
     */
    public SlovoProperties() {
        defaults = generateDefaults();
    }

    // --------------------------------------------------
    // Number of words
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
        setNumberOfWords(String.valueOf(numberOfWords));
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
    // Window width
    // --------------------------------------------------
    /**
     * Returns the window width of the main application.
     *
     * @return the window width.
     */
    public double getWindowWidth() {
        return Double.parseDouble(getProperty(NUMBER_OF_WORDS));
    }

    /**
     * Sets the window width.
     *
     * @param windowWidth the window width.
     */
    public void setWindowWidth(double windowWidth) {
        setWindowWidth(String.valueOf(windowWidth));
    }

    /**
     * Sets the window width of the main application.
     *
     * @param windowWidth the window width.
     */
    public void setWindowWidth(String windowWidth) {
        setProperty(WINDOW_WIDTH, windowWidth);
    }

    /**
     * Returns the default window width of the main application.
     *
     * @return the default window width.
     */
    public double getDefaultWindowWidth() {
        return Double.parseDouble(getProperty(DEFAULT_WINDOW_WIDTH));
    }

    // --------------------------------------------------
    // Window height
    // --------------------------------------------------
    /**
     * Returns the default window height of the main application.
     *
     * @return the window height.
     */
    public double getWindowHeigth() {
        return Double.parseDouble(getProperty(WINDOW_HEIGHT));
    }

    /**
     * Sets the window height.
     *
     * @param windowHeight the window height.
     */
    public void setWindowHeight(double windowHeight) {
        setWindowHeight(String.valueOf(windowHeight));
    }

    /**
     * Sets the window height of the main application.
     *
     * @param windowHeight the window height.
     */
    public void setWindowHeight(String windowHeight) {
        setProperty(WINDOW_HEIGHT, windowHeight);
    }

    /**
     * Returns the default window height of the main application.
     *
     * @return the default window height.
     */
    public double getDefaultWindowHeight() {
        return Double.parseDouble(getProperty(DEFAULT_WINDOW_HEIGHT));
    }

    // --------------------------------------------------
    // Fullscreen mode
    // --------------------------------------------------
    /**
     * Returns whether the application should be displayed in maximized mode.
     *
     * @return the maximized mode.
     */
    public boolean getMaximizedMode() {
        return Boolean.parseBoolean(getProperty(MAXIMIZED_MODE));
    }

    /**
     * Sets the maximized mode setting.
     *
     * @param maximizedMode the maximized mode.
     */
    public void setMaximizedMode(boolean maximizedMode) {
        setMaximizedMode(String.valueOf(maximizedMode));
    }

    /**
     * Sets the maximized mode setting.
     *
     * @param maximizedMode the maximized mode.
     */
    public void setMaximizedMode(String maximizedMode) {
        setProperty(MAXIMIZED_MODE, maximizedMode);
    }

    /**
     * Returns the default maximized mode setting.
     *
     * @return the maximized mode.
     */
    public boolean getDefaultMaximizedMode() {
        return Boolean.parseBoolean(getProperty(DEFAULT_MAXIMIZED_MODE));
    }

    // --------------------------------------------------
    // Overriden methods
    // --------------------------------------------------
    @Override
    public void store(OutputStream out, String comments) throws IOException {
        defaults.entrySet().forEach(e -> {
            if (!this.contains(e.getKey())) {
                setProperty((String) e.getKey(), (String) e.getValue());
            }
        });

        super.store(out, comments);
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
        newDefaults.setProperty(WINDOW_WIDTH, DEFAULT_WINDOW_WIDTH);
        newDefaults.setProperty(WINDOW_HEIGHT, DEFAULT_WINDOW_HEIGHT);
        newDefaults.setProperty(MAXIMIZED_MODE, DEFAULT_MAXIMIZED_MODE);
        return newDefaults;
    }
}
