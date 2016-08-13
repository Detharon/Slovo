package com.dth.slovo.properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PropertiesAccessorTest {
    
    private static final String PROPERTIES_LOCATION = "config\\slovo.properties";
    
    private PropertiesAccessor accessor;

    @Before
    public void setUp() {
        accessor = new PropertiesAccessor(new Properties());
    }
    
    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(PROPERTIES_LOCATION));
    }

    /**
     * Test of generateFile method, of class PropertiesAccessor.
     * 
     * @throws java.io.IOException
     */
    @Test
    public void testGenerateFile() throws IOException {
        accessor.generateFile(null);
        Path path = Paths.get(PROPERTIES_LOCATION);
        assertEquals(Files.exists(path), true);     
    }
    
    /**
     * Test of getFileLocation method, of class PropertiesAccessor.
     */
    @Test
    public void testGetFileLocation() {
        File expectedFile = new File(PROPERTIES_LOCATION);
        File file = accessor.getFileLocation();
        assertEquals(expectedFile.getAbsolutePath(), file.getAbsolutePath());
    }
}
