package com.dth.slovo;

import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.controller.RootController;
import com.dth.slovo.properties.SlovoProperties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Slovo extends Application {

    private static final Logger LOG = Logger.getLogger(Slovo.class.getName());

    private PropertiesAccessor<SlovoProperties> propertiesAccessor;

    @Override
    public void start(Stage stage) throws IOException {
        // Loading logging settings
        try (FileInputStream fis = new FileInputStream("config/logging.properties")) {
            LogManager.getLogManager().readConfiguration(fis);
            LOG.log(Level.INFO, "Loaded the logging.properties file.");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to load the logging.properties configuration file.", ex);
        }

        // Create the necessary properties file, if it's not already present
        propertiesAccessor = new PropertiesAccessor(new SlovoProperties());
        try {
            if (!propertiesAccessor.getFileLocation().exists()) {
                propertiesAccessor.store();
                LOG.log(Level.INFO, "Crated the default slovo.properties file.");
            } else {
                propertiesAccessor.load();
                LOG.log(Level.INFO, "Loaded the slovo.properties file.");
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to create or load the default properties file.", ex);
        }

        // Loads the object definitions and populates the Scene graph
        // If loading the scene fails, program should throw an exception and
        // crash.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Root.fxml"));
        Scene scene;
        scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Slovo");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
        stage.setMaximized(propertiesAccessor.getProperties().getMaximizedMode());
        stage.show();

        RootController rootController = loader.getController();
        rootController.setStage(stage);

        // Database access need to be shut down manually
        stage.setOnCloseRequest(e -> {
            LOG.log(Level.FINE, "Close event received from {0}.", e.getSource());

            try {
                propertiesAccessor.load();
                SlovoProperties properties = propertiesAccessor.getProperties();
                properties.setWindowWidth(rootController.getRoot().getWidth());
                properties.setWindowHeight(rootController.getRoot().getHeight());
                properties.setMaximizedMode(stage.isMaximized());
                propertiesAccessor.store();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Failed to save the properties on exit.", ex);
            }

            rootController.close();
            LOG.log(Level.INFO, "Database connection was closed.");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
