package com.dth.controller;

import com.dth.entity.WordOccurrence;
import com.dth.service.CsvExportWords;
import com.dth.service.ExportWords;
import com.dth.service.WordOccurrenceRepository;
import com.dth.util.DefaultDocumentProcessor;
import com.dth.util.DefaultWordProcessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class RootController implements Initializable {

    private final EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("SlovoPU");
    private Stage stage;

    @FXML
    private TableView table;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label status;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        emfactory.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createTable();
        populateTable(1000, false);
    }

    // --------------------------------------------------
    // File menu
    // --------------------------------------------------
    @FXML
    public void preferencesClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Preferences.fxml"));
            Stage preferencesStage = new Stage();
            preferencesStage.setTitle("Preferences");
            preferencesStage.setScene(new Scene(loader.load()));
            preferencesStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
            preferencesStage.show();
        } catch (IOException ex) {
            Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void openClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File chosen = fileChooser.showOpenDialog(stage);

        if (chosen != null) {
            setBusy();
            new Thread(() -> {
                DefaultDocumentProcessor documentProcessor
                        = new DefaultDocumentProcessor(chosen, new DefaultWordProcessor());
                documentProcessor.processFile();

                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.saveWords(documentProcessor.getWords());

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> setReady());
                populateTable(1000, false);
            }).start();
        }
    }

    @FXML
    public void exportClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to save");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        fileChooser.setInitialFileName("words.txt");
        File chosen = fileChooser.showSaveDialog(stage);

        if (chosen != null) {
            setBusy();
            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                ExportWords export;
                try {
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(chosen), "UTF-8"));

                    WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                    List<WordOccurrence> words = wordRepo.fetchWords(1000);

                    export = new CsvExportWords(writer);
                    export.export(words, 1000);
                    export.close();
                } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                    // TODO handle these exceptions;
                }

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> setReady());
            }).start();
        }
    }

    @FXML
    public void exitClicked() {
        Platform.exit();
    }

    // --------------------------------------------------
    // Data menu
    // --------------------------------------------------
    @FXML
    public void ignoreListClicked() {
        populateTable(1000, true);
    }

    @FXML
    public void wordListClicked() {
        populateTable(1000, false);
    }

    @FXML
    public void eraseAllClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Are you sure you want to remove all data?"
                + "\nThis operation is irreversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            setBusy();

            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.eraseAllWords();

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> setReady());
                populateTable(1000, false);
            }).start();
        }
    }

    // --------------------------------------------------
    // Table
    // --------------------------------------------------
    @FXML
    public void tableKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            List<WordOccurrence> words = table.getSelectionModel().getSelectedItems();
            if (words == null) {
                return;
            }

            setBusy();
            new Thread(() -> {
                for (WordOccurrence w : words) {
                    w.setIgnored(!w.getIgnored());
                }

                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.updateWords(words);

                em.getTransaction().commit();
                em.close();

                table.getItems().removeAll(words);
                Platform.runLater(() -> setReady());
            }).start();
        }
    }

    // --------------------------------------------------
    // General helpers
    // --------------------------------------------------
    private void setReady() {
        status.setText("Status: Ready");
        progressBar.setProgress(100);
    }

    private void setBusy() {
        status.setText("Status: Busy");
        progressBar.setProgress(-1);
    }

    public void populateTable(int rows, boolean ignored) {
        setBusy();
        new Thread(() -> {
            EntityManager em = emfactory.createEntityManager();
            em.getTransaction().begin();

            WordOccurrenceRepository query = new WordOccurrenceRepository(em);
            if (!ignored) {
                table.setItems(FXCollections.observableList(query.fetchWords(rows)));
            } else {
                table.setItems(FXCollections.observableList(query.fetchWords(rows, true)));
            }

            em.getTransaction().commit();
            em.close();

            Platform.runLater(() -> setReady());
        }).start();
    }

    private void createTable() {
        table.setPlaceholder(new Label("No words to show, open a new text file."));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<WordOccurrence, Integer> numCol = new TableColumn("#");
        numCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        numCol.setSortable(false);
        numCol.setCellValueFactory(c
                -> new ReadOnlyObjectWrapper<>(
                        table.getItems().indexOf(c.getValue())));

        TableColumn<WordOccurrence, String> wordCol = new TableColumn("Word");
        wordCol.prefWidthProperty().bind(table.widthProperty().divide(6).multiply(3));
        wordCol.setCellValueFactory(new PropertyValueFactory("word"));

        TableColumn<WordOccurrence, Integer> countCol = new TableColumn("Count");
        countCol.prefWidthProperty().bind(table.widthProperty().divide(6).multiply(2));
        countCol.setCellValueFactory(new PropertyValueFactory("count"));

        table.getColumns().addAll(numCol, wordCol, countCol);
    }
}
