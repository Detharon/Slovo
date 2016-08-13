package com.dth.slovo.properties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Convenience class for generating the default properties file and necessary
 * directories.
 *
 * @param <T> the properties object.
 *
 * @see SlovoProperties
 */
public class PropertiesAccessor<T extends Properties> {

    private final String PROPERTIES_LOCATION = "config\\slovo.properties";
    private final T properties;

    public PropertiesAccessor(T properties) {
        this.properties = properties;
    }

    /**
     * Generates a properties file using the specified writer.
     *
     * @param comments the description of the output file.
     * @throws IOException
     */
    public synchronized void generateFile(String comments) throws IOException {
        File propertiesFile = getFileLocation();
        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(PROPERTIES_LOCATION)) {
                properties.store(fw, comments);
            }
        }
    }

    /**
     * Returns the {@code File} associated with the class, which can be used to
     * load or save the properties file to a predefined location on a hard
     * drive.
     *
     * @return the {@code File} object.
     */
    public File getFileLocation() {
        return Paths.get(Paths.get("").toAbsolutePath().toString(),
                PROPERTIES_LOCATION).toFile();
    }

    /**
     * Attempts to load the properties file from the default location and using
     * a default reader, which is an instance of FileReader class.
     *
     * @throws IOException in case of failure.
     */
    public synchronized void load() throws IOException {
        try (FileReader fr = new FileReader(PROPERTIES_LOCATION)) {
            properties.load(fr);
        }
    }
    
    /**
     * Saves the properties on the default location, using the default reader,
     * which is an instance of FileWriter class.
     * 
     * @throws IOException 
     */
    public synchronized void store() throws IOException {
        try (FileWriter fw = new FileWriter(PROPERTIES_LOCATION)) {
            properties.store(fw, null);
        }
    }
    
    /**
     * Returns the properties object associated with its accessor.
     * 
     * @return the properties.
     */
    public T getProperties() {
        return properties;
    }
}
