package com.dth.controller;

import com.dth.entity.Sentence;
import com.dth.entity.WordOccurrence;
import com.dth.service.SentenceRepository;
import com.dth.service.WordOccurrenceRepository;
import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.slovo.properties.SlovoProperties;
import com.dth.util.DefaultDocumentProcessor;
import com.dth.util.DefaultSentenceProcessor;
import com.dth.util.DefaultWordProcessor;
import com.dth.util.SentenceProcessor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class RootController {

    private static final Logger LOG = Logger.getLogger(RootController.class.getName());

    private final EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("SlovoPU");

    private PropertiesAccessor<SlovoProperties> propertiesAccessor;
    private Stage stage;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<WordOccurrence> table;

    @FXML
    private TableView<Sentence> sentenceTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label status;

    /**
     * Closes the EntityManagerFactory.
     */
    public void close() {
        emfactory.close();
    }

    @FXML
    public void initialize() {
        propertiesAccessor = new PropertiesAccessor<>(new SlovoProperties());
        try {
            propertiesAccessor.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Root controller has failed to load the properties file.", ex);
        }
        SlovoProperties properties = propertiesAccessor.getProperties();
        root.setPrefWidth(properties.getWindowWidth());
        root.setPrefHeight(properties.getWindowHeigth());

        createTable();
        createSentenceTable();
        populateTable(propertiesAccessor.getProperties().getNumberOfWords(), false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Pane getRoot() {
        return root;
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
            preferencesStage.setOnHiding(e -> {
                refresh();
            });
        } catch (IOException ex) {
            Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void openClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File document = fileChooser.showOpenDialog(stage);

        if (document != null) {
            setBusy("Reading");
            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                // Splitting document into sentences
                DefaultDocumentProcessor documentProcessor = new DefaultDocumentProcessor();
                documentProcessor.processDocument(document);
                List<Sentence> sentences = documentProcessor.getSentences();

                // Generating words
                SentenceProcessor<Sentence, WordOccurrence> sentenceProcessor
                        = new DefaultSentenceProcessor(new DefaultWordProcessor());

                for (Sentence s : sentences) {
                    sentenceProcessor.processSentence(s);
                }
                List<WordOccurrence> words = sentenceProcessor.getWords();

                // Saving words
                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                LOG.log(Level.INFO, "Saving words: {0}", words.size());
                wordRepo.saveWords(words);

                // Saving sentences
                LOG.log(Level.INFO, "Saving sentences: {0}", sentences.size());
                SentenceRepository sentenceRepo = new SentenceRepository(em);
                sentenceRepo.saveSentences(sentences);

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> {
                    populateTable(propertiesAccessor.getProperties().getNumberOfWords(), false);
                });
            }).start();
        }
    }

    @FXML
    public void importClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Import.fxml"));
            Stage importStage = new Stage();
            importStage.setTitle("Import words");
            importStage.setScene(new Scene(loader.load()));
            importStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
            importStage.show();
            importStage.setOnHiding(e -> {
                refresh();
            });
            ImportController controller = loader.getController();
            controller.setEntityManagerFactory(emfactory);
        } catch (IOException ex) {
            //TODO: exception
        }
    }

    @FXML
    public void exportClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Export.fxml"));
            Stage exportStage = new Stage();
            exportStage.setTitle("Export words");
            exportStage.setScene(new Scene(loader.load()));
            exportStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
            exportStage.show();
            ExportController controller = loader.getController();
            controller.setEntityManagerFactory(emfactory);
        } catch (IOException ex) {
            //TODO: exception
        }
    }

    @FXML
    public void exitClicked() {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    // --------------------------------------------------
    // Data menu
    // --------------------------------------------------
    @FXML
    public void ignoreListClicked() {
        populateTable(propertiesAccessor.getProperties().getNumberOfWords(), true);
    }

    @FXML
    public void wordListClicked() {
        populateTable(propertiesAccessor.getProperties().getNumberOfWords(), false);
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

                int deleted;

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                deleted = wordRepo.deleteAllWords();
                LOG.log(Level.INFO, "Deleted {0} words.", deleted);

                SentenceRepository sentenceRepo = new SentenceRepository(em);
                deleted = sentenceRepo.deleteAllSentences();
                LOG.log(Level.INFO, "Deleted {0} sentences.", deleted);

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> setReady()); //TODO: not on FX thread error
                populateTable(propertiesAccessor.getProperties().getNumberOfWords(), false);
            }).start();
        }
    }

    // --------------------------------------------------
    // Table
    // --------------------------------------------------
    @FXML
    protected void tableKeyPressed(KeyEvent event) {
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

    private void setBusy(String message) {
        status.setText("Status: " + message);
        progressBar.setProgress(-1);
    }

    private void populateTable(int rows, boolean ignored) {
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
                        1 + table.getItems().indexOf(c.getValue())));

        TableColumn<WordOccurrence, String> wordCol = new TableColumn("Word");
        wordCol.prefWidthProperty().bind(table.widthProperty().divide(6).multiply(3));
        wordCol.setCellValueFactory(new PropertyValueFactory("word"));

        TableColumn<WordOccurrence, Integer> countCol = new TableColumn("Count");
        countCol.prefWidthProperty().bind(table.widthProperty().divide(6).multiply(2));
        countCol.setCellValueFactory(new PropertyValueFactory("count"));

        table.getColumns().addAll(numCol, wordCol, countCol);

        // Responsible for displaying sentences based on the selected word
        table.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null) {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                int index = table.getSelectionModel().getSelectedIndex();
                if (index > -1) {
                    WordOccurrence word = em.merge(table.getItems().get(index));
                    sentenceTable.setItems(FXCollections.observableArrayList(word.getSentences()));
                }
                em.close();
            }
        });
    }

    private void createSentenceTable() {
        sentenceTable.setPlaceholder(new Label("No sentences to show, select a word."));

        TableColumn<Sentence, Integer> numCol = new TableColumn("#");
        numCol.prefWidthProperty().bind(sentenceTable.widthProperty().divide(10));
        numCol.setSortable(false);
        numCol.setCellValueFactory(c
                -> new ReadOnlyObjectWrapper<>(
                        1 + sentenceTable.getItems().indexOf(c.getValue())));

        TableColumn<Sentence, String> sentenceCol = new TableColumn("Sentence");
        sentenceCol.prefWidthProperty().bind(sentenceTable.widthProperty().divide(10).multiply(9));
        sentenceCol.setCellValueFactory(new PropertyValueFactory("sentence"));

        sentenceTable.getColumns().addAll(numCol, sentenceCol);
    }

    private void refresh() {
        try {
            propertiesAccessor.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to load the properties file.", ex);
        }

        populateTable(propertiesAccessor.getProperties().getNumberOfWords(), false);
    }
}
