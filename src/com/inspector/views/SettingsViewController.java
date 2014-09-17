/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.views;

import com.inspector.MainApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Даша
 */
public class SettingsViewController {

    private Stage dialogStage;
    private boolean okClicked = false;
    @FXML
    private ChoiceBox statusChoiceBox;
    @FXML
    private ChoiceBox changesChoiceBox;   
    @FXML
    private TextField statusSecField;
    @FXML
    private TextField statusMinField;
    @FXML
    private TextField statusHourField;
    @FXML
    private TextField changeField;
    
    private MainApp mainApp;
    
    @FXML
    public void initialize() {

//        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                mainApp.getPreferences().getUserPrefs().putInt("item", Integer.parseInt(newValue.toString()));
//                
//            }
//        });
    }    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    @FXML
    private void handleOk() {
        int l = Integer.parseInt(statusSecField.getText());
        int k = Integer.parseInt(statusMinField.getText());
        int j = Integer.parseInt(statusHourField.getText());
        int microSeconds = Integer.parseInt(statusSecField.getText())*1000+Integer.parseInt(statusMinField.getText())*60000+Integer.parseInt(statusHourField.getText())*1000*60*60;
        mainApp.getPreferences().setStatusFrequency(String.valueOf(microSeconds));
        okClicked = true;
        dialogStage.close();
        
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        int hours = Integer.parseInt(mainApp.getPreferences().getStatusFrequency())/1000/60/60;
        int minutes = (Integer.parseInt(mainApp.getPreferences().getStatusFrequency())-hours*60*60*1000)/60/1000;
        int seconds = (Integer.parseInt(mainApp.getPreferences().getStatusFrequency())-hours*60*60*1000-minutes*60*1000)/1000;
        statusSecField.setText(String.valueOf(seconds));
        statusMinField.setText(String.valueOf(minutes));
        statusHourField.setText(String.valueOf(hours));
    }
    
    public void setSettings(){
        
    }
    public boolean isOkClicked() {
        return okClicked;
    }
}
