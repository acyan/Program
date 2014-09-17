/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.views;

import javafx.fxml.FXML;
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
    public void initialize() {
        // TODO
    }    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    @FXML
    private void handleOk() {
            okClicked = true;
            dialogStage.close();
        
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
}
