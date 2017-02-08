package com.dth.util;

/**
 * DefaultWordProcessor is a small class that normalizes the {@code String} by
 * altering it in numerous ways, so that the resulting {@code String} will only
 * contain a sequence of alphabetic characters.
 *
 * <p>
 * For example, in the following code, strings {code "a"}, {code "b"}, and {code
 * "c"} will contain "happy" in all cases.
 * <blockquote>
 * WordProcessor processor = new WordProcessor(); String a =
 * processor.processWord(" happy "); String b =
 * processor.processWord(".happy!"); String c = processor.processWord("
 * HaPPy!");
 * </blockquote>
 */
public class DefaultWordProcessor implements WordProcessor<String> {

    /**
     * Processes the {@code String} by subjecting it to the following changes:
     * <ul>
     * <li> Removing leading and trailing white spaces
     * <li> Removing leading and trailing punctuation marks
     * <li> Converting to lowercase.
     * </ul>
     *
     * @param word the input {@code String} that will be processed.
     *
     * @return the normalized {@code String}, if the input contained at least
     * one alphabetic character. Otherwise returns an empty {@code String}.
     */
    @Override
    public String processWord(String word) {
        // Initialize the StringBuilder and trim the string
        StringBuilder temp = new StringBuilder(word.trim());

        // Remove leading punctuation marks               
        while (temp.length() > 0 && !Character.isAlphabetic(temp.charAt(0))) {
            temp.deleteCharAt(0);
        }

        // Remove trailing punctuation marks  
        while (temp.length() > 0 && !Character.isAlphabetic(temp.charAt(temp.length() - 1))) {
            temp.deleteCharAt(temp.length() - 1);
        }

        // To lowercase
        for (int i = 0; i < temp.length(); i++) {
            temp.setCharAt(i, Character.toLowerCase(temp.charAt(i)));
        }

        return temp.toString();
    }
}
