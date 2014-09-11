/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.views;

import com.inspector.MainApp;
import com.inspector.model.Site;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author dasha
 */
public class SiteOverviewController{
    
    @FXML
    private TableView<Site> siteTable;
    @FXML
    private TableColumn<Site, String> addressColumn;
    @FXML
    private TableColumn<Site, String> statusColumn;
    @FXML
    private Label addressLabel;
    @FXML
    private Label changesLabel;
    
    private MainApp mainApp;

    public SiteOverviewController() {
    }
 
    
    @FXML
    public void initialize() {
        addressColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        
        showPersonDetails(null);
        
        siteTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Site> observable, Site oldValue, Site newValue) -> showPersonDetails(newValue));
        
    }    
    
    public void setMainApp(MainApp mainApp){
        this.mainApp=mainApp;
        
        siteTable.setItems(mainApp.getSites());
    }
    private void showPersonDetails(Site site) {
    	if (site != null) {
            addressLabel.setText(site.getName());
            if(site.getChange())
                changesLabel.setText("Да");
            else {
                changesLabel.setText("Нет");
            }
            
    	} else {
            addressLabel.setText("");
            changesLabel.setText("");
    	}
    }
}
