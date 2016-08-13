package com.dth.controller;

import com.dth.slovo.properties.PropertiesAccessor;
import com.dth.slovo.properties.SlovoProperties;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class PreferencesController implements Initializable {

    private PropertiesAccessor<SlovoProperties> propertiesAccessor;

    @FXML
    private TextField numberOfWords;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        propertiesAccessor = new PropertiesAccessor<>(new SlovoProperties());

        try {
            propertiesAccessor.load();
        } catch (IOException ex) {
            // TODO: error message
        }

        setNumberFormatter(numberOfWords);
        numberOfWords.setText(String.valueOf(propertiesAccessor.getProperties().getNumberOfWords()));
    }

    @FXML
    public void saveClicked() {
        try {
            propertiesAccessor.getProperties().setProperty(SlovoProperties.NUMBER_OF_WORDS, numberOfWords.getText());
            propertiesAccessor.store();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        numberOfWords.getScene().getWindow().hide();
    }

    @FXML
    public void cancelClicked() {
        ((Stage) numberOfWords.getScene().getWindow()).close();
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------
    private void setNumberFormatter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>((c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            DecimalFormat format = new DecimalFormat("#");
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        })));
    }
}
