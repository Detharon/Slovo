package com.dth.controller;

import com.dth.entity.WordOccurrence;
import com.dth.service.transfer.CsvExportWords;
import com.dth.service.transfer.TransferFailedException;
import com.dth.service.transfer.ExportWords;
import com.dth.service.WordOccurrenceRepository;
import com.dth.service.transfer.XmlExportWords;
import com.dth.service.transfer.TransferModes;
import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.slovo.properties.SlovoProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;

public class ExportController implements Initializable {

    private static final Logger LOG = Logger.getLogger(ExportController.class.getName());

    private static final Charset[] ENCODINGS = {
        StandardCharsets.UTF_8,
        StandardCharsets.UTF_16,
        StandardCharsets.ISO_8859_1
    };

    private PropertiesAccessor<SlovoProperties> propertiesAccessor;
    private EntityManagerFactory emfactory;

    @FXML
    ComboBox<TransferModes> exportMode;

    @FXML
    ComboBox<Charset> encoding;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        propertiesAccessor = new PropertiesAccessor<>(new SlovoProperties());

        try {
            propertiesAccessor.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to load the properties file.", ex);
        }

        initializeExportModeComboBox();
        initializeEncodingComboBox();
    }

    private void initializeExportModeComboBox() {
        exportMode.setItems(FXCollections.observableList(Arrays.asList(TransferModes.values())));
        exportMode.getSelectionModel().selectFirst();

        exportMode.setOnAction(e -> {
            if (exportMode.getSelectionModel().getSelectedItem() == TransferModes.XML) {
                encoding.setDisable(true);
            } else {
                encoding.setDisable(false);
            }
        });
    }

    private void initializeEncodingComboBox() {
        encoding.setItems(FXCollections.observableList(Arrays.asList(ENCODINGS)));
        encoding.getSelectionModel().selectFirst();
    }

    @FXML
    public void exportClicked() {
        String extension = "";
        switch (exportMode.getSelectionModel().getSelectedItem()) {
            case CSV:
                extension = "csv";
                break;
            case XML:
                extension = "xml";
                break;
        }
        String filter = "*." + extension;
        String filename = "words." + extension;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to export to");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", filter));
        fileChooser.setInitialFileName(filename);
        File chosen = fileChooser.showSaveDialog(exportMode.getScene().getWindow());

        if (chosen != null) {
            EntityManager em = emfactory.createEntityManager();
            em.getTransaction().begin();

            WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
            List<WordOccurrence> words = wordRepo.fetchWords(propertiesAccessor.getProperties().getNumberOfWords());
            em.close();

            ExportWords export;
            switch (exportMode.getSelectionModel().getSelectedItem()) {
                case CSV:
                    Charset charset = encoding.getSelectionModel().getSelectedItem();
                    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(chosen), charset)) {
                        export = new CsvExportWords(writer);
                        export.exportWords(words);
                    } catch (TransferFailedException ex) {
                        LOG.log(Level.SEVERE, "Failed to parse the words to be exported.", ex);
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, "Failed to close the writer.", ex);
                    }
                    break;
                case XML:
                    try {
                        StreamResult streamResult = new StreamResult(chosen);
                        export = new XmlExportWords(streamResult);
                        export.exportWords(words);
                    } catch (ParserConfigurationException | TransferFailedException ex) {
                        LOG.log(Level.SEVERE, "Failed to parse the words to be exported.", ex);
                    }
                    break;
            }
        }
    }

    @FXML
    public void cancelClicked() {
        ((Stage) exportMode.getScene().getWindow()).close();
    }

    public void setEntityManagerFactory(EntityManagerFactory emfactory) {
        this.emfactory = emfactory;
    }
}
