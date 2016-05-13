package com.dth.slovo;

import com.dth.controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Slovo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // Loads the object definitions and populates the Scene graph
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Root.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        
        // Passing stage so that the controller can show dialogs
        RootController controller = loader.getController();
        controller.setStage(stage);  
        
        // Database access need to be shut down manually
        stage.setOnCloseRequest(WindowEvent -> {
            controller.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}