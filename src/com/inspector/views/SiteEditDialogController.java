/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.views;

import com.inspector.model.Site;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Даша
 */
public class SiteEditDialogController {

    @FXML
    private TextField addressField;
    @FXML
    private RadioButton yesRadioButton;
    @FXML
    private RadioButton noRadioButton;
    @FXML
    private ListView<String> pagesList;
    @FXML
    private TextField pageName;
    
    private Stage dialogStage;
    private Site site;
    private boolean okClicked = false;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        
    }    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    } 
    
    public void setSite(Site site){
        this.site=site;
        
        addressField.setText(site.getName());
        if(site.getChange()){
            yesRadioButton.setSelected(true);
        }else{
            noRadioButton.setSelected(true);
        }
        pagesList.setItems(site.pagesProperty());
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleAdd(){
        site.pagesProperty().add(pageName.getText());
        pageName.setText("");
    }
    
    @FXML
    private void handleDelete(){
            int selectedIndex = pagesList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                    pagesList.getItems().remove(selectedIndex);

                    
            } else {
                    // Nothing selected.
                    Dialogs.create()
                    .title("No Selection")
                    .masthead("No Person Selected")
                    .message("Please select a person in the table.")
                    .showWarning();
            }
    }
    @FXML
    private void handleOk() {
        String j=addressField.getText();
            site.setName(addressField.getText());
            site.setChange(yesRadioButton.isSelected());
            
            okClicked = true;
            dialogStage.close();
        
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
