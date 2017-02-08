package com.dth.service.transfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ImportWordsFactory {

    public static ImportWords createImportWords(TransferModes mode, File file, Charset charset) throws TransferFailedException {
        ImportWords importWords;

        switch (mode) {
            // Default mode is CSV
            default:
                importWords = createCsvImportWords(file, charset);
                break;
            case XML:
                importWords = createXmlImportWords(file);
                break;
        }

        return importWords;
    }
    
    public static ImportWords createImportWords(TransferModes mode, File file) throws TransferFailedException {
        return createImportWords(mode, file, StandardCharsets.UTF_8);
    }

    // --------------------------------------------------
    // Object creation
    // --------------------------------------------------
    private static ImportWords createCsvImportWords(File file, Charset charset) throws TransferFailedException {
        ImportWords importWords;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            importWords = new CsvImportWords(reader);
        } catch (IOException ex) {
            throw new TransferFailedException(ex);
        }

        return importWords;
    }

    private static ImportWords createXmlImportWords(File file) throws TransferFailedException {
        ImportWords importWords;

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(file);
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
            importWords = new XmlImportWords(streamReader);
        } catch (FileNotFoundException | XMLStreamException ex) {
            throw new TransferFailedException(ex);
        }

        return importWords;
    }
}
