/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class XmlExportWordsTest {
    
    XmlExportWords exportWords;
    
    public XmlExportWordsTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of exportWords method, of class XmlExportWords.
     */
    @Test
    public void testExportWords() throws Exception {
        System.out.println("exportWords");
        List<WordOccurrence> words = null;
        XmlExportWords instance = null;
        instance.exportWords(words);
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }
    
}
