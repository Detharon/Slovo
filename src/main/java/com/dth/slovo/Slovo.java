package com.dth.slovo;

import com.dth.entity.WordOccurrence;
import com.dth.service.CsvExportWords;
import com.dth.service.ExportWords;
import com.dth.service.WordOccurrenceRepository;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Slovo extends Application {
    private static final String PERSISTENCE_UNIT = "SlovoPU";
    
    private static BorderPane root = new BorderPane();
    private static Scene scene = new Scene(root, 300, 250);
    private static Stage stage;
    
    private static TableView<WordOccurrence> tableView;
    private static Label status;
    private static ProgressBar progressBar;
    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        createGUI();
        populateTable(1000, false);

        stage.setTitle("Slovo");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
// --------------------------------------------------
// GUI helpers
// --------------------------------------------------
    
    private static void createGUI() {
        // Menu bar
        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);
        
        createFileMenu(menuBar);
        createDataMenu(menuBar);
        
        // Table
        createTable();
        root.setCenter(tableView);
        
        // Progress bar
        status = new Label("Status: Ready");
        progressBar = new ProgressBar(100);
        
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(5,5,5,5));
        hBox.getChildren().addAll(status, progressBar);
        
        root.setBottom(hBox);
    }
    
    private static void createFileMenu(MenuBar menuBar) {
        // File menu item
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction((ActionEvent event) -> {
            openMenuItemClicked();
        });
        
        // Export menu item
        MenuItem exportMenuItem = new MenuItem("Export");
        exportMenuItem.setOnAction((ActionEvent event) -> {
            exportMenuItemClicked();
        });
        
        // Exit menu item
        MenuItem exitMenuItem = new MenuItem("Exit");   
        exitMenuItem.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        
        fileMenu.getItems().addAll(openMenuItem, exportMenuItem, exitMenuItem);
        
        menuBar.getMenus().add(fileMenu);
    }
    
    private static void createDataMenu(MenuBar menuBar) {
        // Erase all menu item
        Menu dataMenu = new Menu("Data");
        MenuItem eraseMenuItem = new MenuItem("Erase all");
        eraseMenuItem.setOnAction((ActionEvent event) -> {
            eraseAllMenuItemClicked();
        });
        
        
        // Show words menu item
        MenuItem wordListMenuItem = new MenuItem("Word list");
        wordListMenuItem.setOnAction((ActionEvent event) -> {
            populateTable(1000, false);
        });
        
        // Show ignore list menu item
        MenuItem ignoreListMenuItem = new MenuItem("Ignore list");
        ignoreListMenuItem.setOnAction((ActionEvent event) -> {
            ignoreListMenuItemClicked();
        });
        
        dataMenu.getItems().addAll(eraseMenuItem, wordListMenuItem, ignoreListMenuItem);
        
        menuBar.getMenus().add(dataMenu);
    }
    
    private static void createTable() {
        tableView = new TableView();
        tableView.setPlaceholder(new Label("No words to show, open a new text file."));
        
        tableView.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeWords();
            }
        });
        
        TableColumn<WordOccurrence, Integer> numCol = new TableColumn("#");
        numCol.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        numCol.setSortable(false);
        numCol.setCellValueFactory(c
                -> new ReadOnlyObjectWrapper<>(
                        tableView.getItems().indexOf(c.getValue())));
        
        TableColumn<WordOccurrence, String> wordCol = new TableColumn("Word");
        wordCol.prefWidthProperty().bind(tableView.widthProperty().divide(6).multiply(3));
        wordCol.setCellValueFactory(new PropertyValueFactory("word"));
        
        TableColumn<WordOccurrence, Integer> countCol = new TableColumn("Count");
        countCol.prefWidthProperty().bind(tableView.widthProperty().divide(6).multiply(2));
        countCol.setCellValueFactory(new PropertyValueFactory("count"));
        
        tableView.getColumns().addAll(numCol, wordCol, countCol);
    }
    
// --------------------------------------------------
// Menu events
// --------------------------------------------------
    
    private static void openMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Text files", "*.txt"));
        File chosen = fileChooser.showOpenDialog(stage);
        
        if (chosen != null) {
            setBusy();
            new Thread(() -> {
                DocumentProcessor documentProcessor = new DocumentProcessor(chosen);
                documentProcessor.processFile();
                
                EntityManagerFactory emfactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();
                
                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.saveWords(documentProcessor.getWords());
                        
                em.getTransaction().commit();
                em.close();
                emfactory.close();
                
                Platform.runLater(() -> setReady());
                populateTable(1000, false);
            }).start();
        }
    }
    
    private static void eraseAllMenuItemClicked() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Are you sure you want to remove all data?"
                + "\nThis operation is irreversible.");  
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            setBusy();
            
            new Thread(() -> {
                EntityManagerFactory emfactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                EntityManager em = emfactory.createEntityManager();
                em.getTransaction().begin();

                WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
                wordRepo.eraseAllWords();

                em.getTransaction().commit();
                em.close();
                emfactory.close();
                
                Platform.runLater(() -> setReady());
                populateTable(1000, false);
            }).start();
        }
    }
    
    private static void exportMenuItemClicked() {
        // TODO: Check whether there are words to be saved
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to save");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Text files", "*.txt"));
        fileChooser.setInitialFileName("words.txt");
        File chosen = fileChooser.showSaveDialog(stage);
        
        if (chosen != null) {
            setBusy();
            new Thread(() -> {
                EntityManagerFactory emfactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
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
                emfactory.close();

                Platform.runLater(() -> setReady());
            }).start();
        }
    }
    
    private static void ignoreListMenuItemClicked() {
        populateTable(1000, true);
    }
   
// --------------------------------------------------
// General helpers
// --------------------------------------------------
    
    private static void setReady() {
        status.setText("Status: Ready");
        progressBar.setProgress(100);
    }
    
    private static void setBusy() {
        status.setText("Status: Busy");
        progressBar.setProgress(-1);
    }
    
    private static void removeWords() {
        List<WordOccurrence> words = tableView.getSelectionModel().getSelectedItems();
        if (words == null) {
            return;
        }

        setBusy();
        new Thread(() -> {
            for (WordOccurrence w : words) {
                w.setIgnored(!w.getIgnored());
            }

            EntityManagerFactory emfactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            EntityManager em = emfactory.createEntityManager();
            em.getTransaction().begin();

            WordOccurrenceRepository wordRepo = new WordOccurrenceRepository(em);
            wordRepo.updateWords(words);

            em.getTransaction().commit();
            em.close();
            emfactory.close();
            
            tableView.getItems().removeAll(words);
            Platform.runLater(() -> setReady());
        }).start();
    }

    public static void populateTable(int rows, boolean ignored) {
        setBusy();
        new Thread(() -> {
            EntityManagerFactory emfactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            EntityManager em = emfactory.createEntityManager();
            em.getTransaction().begin();

            WordOccurrenceRepository query = new WordOccurrenceRepository(em);
            if (!ignored) {
                tableView.setItems(FXCollections.observableList(query.fetchWords(rows)));
            } else {
                tableView.setItems(FXCollections.observableList(query.fetchIgnoredWords(rows)));
            }

            em.getTransaction().commit();
            em.close();
            emfactory.close();

            Platform.runLater(() -> setReady());
        }).start();
    }
}
