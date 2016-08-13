package com.dth.slovo;

import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.controller.RootController;
import com.dth.slovo.properties.SlovoProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Slovo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Create the necessary properties file, if it's not already present
        PropertiesAccessor<SlovoProperties> propertiesAccessor
                = new PropertiesAccessor(new SlovoProperties());
        propertiesAccessor.generateFile("Slovo properties");
        
        // Loads the object definitions and populates the Scene graph
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Root.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Slovo");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
        stage.show();
        
        RootController rootController = loader.getController();
        rootController.setStage(stage);

        // Database access need to be shut down manually
        stage.setOnCloseRequest(e -> {
            rootController.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
