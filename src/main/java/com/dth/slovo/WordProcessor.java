package com.dth.slovo;

public class WordProcessor {
    /** Class
     * 
     */
    public WordProcessor(){}
    
    public String processWord(String word) {
        // Initialize StringBuilder and trim the string
        StringBuilder temp = new StringBuilder(word.trim());
        
        // If its empty, return null
        if (temp.length() == 0) return null;

        // Remove leading punctuation marks               
        while (temp.length() > 0 && !Character.isAlphabetic(temp.charAt(0))) {
            temp.deleteCharAt(0);
        }
        if (temp.length() == 0) return null;
        
        // Remove trailing punctuation marks  
        while (temp.length() > 0 && !Character.isAlphabetic(temp.charAt(temp.length() - 1))) {
            temp.deleteCharAt(temp.length() - 1);
        }
        if (temp.length() == 0) return null;
        
        // To lowercase
        for (int i = 0; i < temp.length(); i++) {
            char c = temp.charAt(i);
            temp.setCharAt(i, Character.toLowerCase(c));
        }
        
        return temp.toString();
    }
}
