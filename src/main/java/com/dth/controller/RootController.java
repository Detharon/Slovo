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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    private MenuBar menuBar;

    @FXML
    private TableView<WordOccurrence> wordTable;
    @FXML
    private ContextMenu wordTableContextMenu;

    @FXML
    private TableView<Sentence> sentenceTable;
    @FXML
    private ContextMenu sentenceTableContextMenu;

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

        createWordTable();
        createSentenceTable();
        refresh();
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
            enableGUI(false);
            new Thread(() -> {
                long startTime = System.currentTimeMillis();

                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                // Splitting document into sentences
                DefaultDocumentProcessor documentProcessor = new DefaultDocumentProcessor();
                try {
                    documentProcessor.processDocument(document);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Failed to read sentences from a file.", ex);
                }
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
                em.flush();

                // Saving sentences
                LOG.log(Level.INFO, "Saving sentences: {0}", sentences.size());
                SentenceRepository sentenceRepo = new SentenceRepository(em);
                sentenceRepo.saveSentences(sentences, wordRepo.findAll());

                em.getTransaction().commit();
                em.close();
                refresh();

                enableGUI(true);
                long endTime = System.currentTimeMillis();
                LOG.log(Level.INFO, "Time elapsed to read the file: {0}ms", endTime - startTime);
            }).start();
        }
    }

    @FXML
    public void importWordsClicked() {
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
            LOG.log(Level.SEVERE, "Failed to load the Import.fxml controller.", ex);
        }
    }

    @FXML
    public void exportWordsClicked() {
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
            LOG.log(Level.SEVERE, "Failed to load the Export.fxml controller.", ex);
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
            enableGUI(false);

            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                int deleted;

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                deleted = wordRepo.deleteAll();
                LOG.log(Level.INFO, "Deleted {0} words.", deleted);

                SentenceRepository sentenceRepo = new SentenceRepository(em);
                deleted = sentenceRepo.deleteAll();
                LOG.log(Level.INFO, "Deleted {0} sentences.", deleted);

                em.getTransaction().commit();
                em.close();

                Platform.runLater(() -> setReady());

                // Refresh the wordTable with words
                refresh();
                enableGUI(true);

                // Clear the sentence selection
                sentenceTable.setItems(FXCollections.observableArrayList());
            }).start();
        }
    }

    // --------------------------------------------------
    // Word Table
    // --------------------------------------------------
    private void createWordTable() {
        wordTable.setPlaceholder(new Label("No words to show, open a new text file."));
        wordTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<WordOccurrence, Integer> numCol = new TableColumn("#");
        numCol.prefWidthProperty().bind(wordTable.widthProperty().divide(6));
        numCol.setSortable(false);
        numCol.setCellValueFactory(c
                -> new ReadOnlyObjectWrapper<>(
                        1 + wordTable.getItems().indexOf(c.getValue())));

        TableColumn<WordOccurrence, String> wordCol = new TableColumn("Word");
        wordCol.prefWidthProperty().bind(wordTable.widthProperty().divide(6).multiply(3));
        wordCol.setCellValueFactory(new PropertyValueFactory("word"));

        TableColumn<WordOccurrence, Integer> countCol = new TableColumn("Count");
        countCol.prefWidthProperty().bind(wordTable.widthProperty().divide(6).multiply(2));
        countCol.setCellValueFactory(new PropertyValueFactory("count"));

        wordTable.getColumns().addAll(numCol, wordCol, countCol);

        // Responsible for displaying sentences based on the selected word
        wordTable.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null) {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                int index = wordTable.getSelectionModel().getSelectedIndex();
                if (index > -1) {
                    WordOccurrence word = em.merge(wordTable.getItems().get(index));
                    sentenceTable.setItems(FXCollections.observableArrayList(word.getSentences()));
                }
                em.close();
            }
        });
    }

    private void selectedWordsToClipboard() {
        if (!wordTable.getSelectionModel().isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            List<WordOccurrence> words = wordTable.getSelectionModel().getSelectedItems();

            StringBuilder wordString = new StringBuilder();
            for (int i = 0; i < words.size(); i++) {
                wordString.append(words.get(i).getWord());

                if (i != words.size() - 1) {
                    wordString.append(", ");
                }
            }

            content.putString(wordString.toString());
            clipboard.setContent(content);
        }
    }

    private void deleteSelectedWords() {
        if (!wordTable.getSelectionModel().isEmpty()) {
            setBusy();
            enableGUI(false);
            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository repo = new WordOccurrenceRepository(em);

                int result;
                if (wordTable.getSelectionModel().getSelectedItems().size() == 1) {
                    result = repo.delete(wordTable.getSelectionModel().getSelectedItem());
                } else {
                    result = repo.delete(wordTable.getSelectionModel().getSelectedItems());
                }

                LOG.log(Level.INFO, "Deleted words: {0}", result);
                
                em.getTransaction().commit();
                em.close();

                refresh();
                enableGUI(true);
            }).start();
        }
    }

    // --------------------------------------------------
    // Sentence Table
    // --------------------------------------------------
    private void createSentenceTable() {
        sentenceTable.setPlaceholder(new Label("No sentences to show, select a word."));
        sentenceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

    private void selectedSentencesToClipboard() {
        if (!sentenceTable.getSelectionModel().isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            
            List<Sentence> sentences = sentenceTable.getSelectionModel().getSelectedItems();
            
            StringBuilder sentenceString = new StringBuilder();
            for (int i = 0; i < sentences.size(); i++) {
                sentenceString.append(sentences.get(i).getSentence());

                if (i != sentences.size() - 1) {
                    sentenceString.append(", ");
                }
            }            
            
            String sentence = sentenceTable.getSelectionModel().getSelectedItem().getSentence();
            content.putString(sentence);
            clipboard.setContent(content);
        }
    }

    private void deleteSelectedSentences() {
        if (!sentenceTable.getSelectionModel().isEmpty()) {
            setBusy();
            enableGUI(false);

            new Thread(() -> {
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                SentenceRepository repo = new SentenceRepository(em);

                int result;
                if (sentenceTable.getSelectionModel().getSelectedItems().size() == 1) {
                    result = repo.delete(sentenceTable.getSelectionModel().getSelectedItem());
                } else {
                    result = repo.delete(sentenceTable.getSelectionModel().getSelectedItems());
                }

                LOG.log(Level.INFO, "Deleted sentences: {0}", result);

                em.getTransaction().commit();
                em.close();
                
                refresh();
                enableGUI(true);
            }).start();
        }
    }

    // --------------------------------------------------
    // Context Menus
    // --------------------------------------------------        
    @FXML
    public void copyClicked() {
        if (sentenceTable.isFocused()) {
            selectedSentencesToClipboard();
        } else if (wordTable.isFocused()) {
            selectedWordsToClipboard();
        }
    }

    @FXML
    public void ignoreClicked() {
        List<WordOccurrence> words = wordTable.getSelectionModel().getSelectedItems();
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

            wordTable.getItems().removeAll(words);
            Platform.runLater(() -> setReady());
        }).start();
    }

    @FXML
    public void deleteClicked() {
        if (sentenceTable.isFocused()) {
            deleteSelectedSentences();
        } else if (wordTable.isFocused()) {
            deleteSelectedWords();
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

    private void enableGUI(boolean enable) {
        menuBar.setDisable(!enable);
        // wordTableContextMenu.getItems().forEach(m -> {m.setDisable(!enable);});
        // sentenceTableContextMenu.getItems().forEach(m -> {m.setDisable(!enable);});
    }

    private void populateTable(int rows, boolean ignored) {
        Platform.runLater(() -> setBusy());

        new Thread(() -> {
            EntityManager em = emfactory.createEntityManager();
            em.getTransaction().begin();

            WordOccurrenceRepository query = new WordOccurrenceRepository(em);
            List<WordOccurrence> words;
            if (!ignored) {
                words = query.fetchWords(rows);
            } else {
                words = query.fetchWords(rows, true);
            }

            em.getTransaction().commit();
            em.close();

            Platform.runLater(() -> {
                wordTable.setItems(FXCollections.observableList(words));
                setReady();
            });
        }).start();
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
