package com.dth.controller;

import com.dth.entity.WordOccurrence;
import com.dth.service.transfer.ImportWords;
import com.dth.service.WordOccurrenceRepository;
import com.dth.service.transfer.ImportWordsFactory;
import com.dth.service.transfer.TransferFailedException;
import com.dth.service.transfer.TransferModes;
import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.slovo.properties.SlovoProperties;
import java.io.File;
import java.io.IOException;
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

public class ImportController implements Initializable {

    private static final Logger LOG = Logger.getLogger(ImportController.class.getName());

    private PropertiesAccessor<SlovoProperties> propertiesAccessor;
    private EntityManagerFactory emfactory;

    @FXML
    ComboBox<TransferModes> importMode;

    @FXML
    ComboBox<Charset> encoding;

    private static final Charset[] ENCODINGS = {
        StandardCharsets.UTF_8,
        StandardCharsets.UTF_16,
        StandardCharsets.ISO_8859_1
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        propertiesAccessor = new PropertiesAccessor<>(new SlovoProperties());

        try {
            propertiesAccessor.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to load the properties file.", ex);
        }

        initializeImportModeComboBox();
        initializeEncodingComboBox();
    }

    private void initializeImportModeComboBox() {
        importMode.setItems(FXCollections.observableList(Arrays.asList(TransferModes.values())));
        importMode.getSelectionModel().selectFirst();

        importMode.setOnAction(e -> {
            if (importMode.getSelectionModel().getSelectedItem() == TransferModes.XML) {
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
    public void importClicked() {
        String extension = importMode.getSelectionModel().getSelectedItem().getName().toLowerCase();
        String filter = "*." + extension;
        String filename = "words." + extension;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to import from");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", filter));
        fileChooser.setInitialFileName(filename);
        File chosen = fileChooser.showOpenDialog(importMode.getScene().getWindow());

        if (chosen != null) {
            ImportWords importWords;

            try {
                importWords = ImportWordsFactory.createImportWords(
                        importMode.getSelectionModel().getSelectedItem(),
                        chosen,
                        encoding.getSelectionModel().getSelectedItem());

                List<WordOccurrence> words = importWords.importWords();
                importWords.close();

                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.saveWords(words);
                em.getTransaction().commit();
                em.close();
            } catch (TransferFailedException ex) {
                LOG.log(Level.SEVERE, "Failed to read the file to be imported.", ex);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Failed to close the reader.", ex);
            }

            importMode.getScene().getWindow().hide();
        }
    }

    @FXML
    public void cancelClicked() {
        ((Stage) importMode.getScene().getWindow()).close();
    }

    public void setEntityManagerFactory(EntityManagerFactory emfactory) {
        this.emfactory = emfactory;
    }
}
