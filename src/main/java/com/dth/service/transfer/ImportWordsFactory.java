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

/**
 * A factory of {@link com.dth.service.transfer.ImportWords} objects.
 */
public class ImportWordsFactory {

    /**
     * Creates the {@link ImportWords} object responsible for importing the
     * words.
     *
     * @see com.dth.service.transfer.TransferModes
     *
     * @param mode the {@code TransferMode}.
     * @param file the file, where words will be saved.
     * @param charset the character-encoding scheme used.
     * @return the {@code ImportWords} object.
     * @throws TransferFailedException
     */
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

    /**
     * Creates the {@code ImportWords} object responsible for importing the
     * words, using the default (UTF-8) encoding.
     *
     * @see com.dth.service.transfer.TransferModes
     *
     * @param mode the {@code TransferMode}.
     * @param file the file, where words will be saved.
     * @return the {@code ImportWords} object.
     * @throws TransferFailedException
     */
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
