/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.views;

import com.inspector.MainApp;
import com.inspector.model.Page;
import com.inspector.model.Site;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import org.controlsfx.dialog.Dialogs;
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
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<Page> pagesList;
    @FXML
    private TableColumn<Page, String> pageAddressColumn;
    @FXML
    private TableColumn<Page, String> pageStatusColumn;
    @FXML
    private Label labelTest;
    
    private MainApp mainApp;

    private SimpleStringProperty value;
    
    public SiteOverviewController() {
    }
 
    
    @FXML
    public void initialize() {
        
        addressColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        
        pageAddressColumn.setCellValueFactory(data->data.getValue().nameProperty());
        pageStatusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        
        showSiteDetails(null);
        
        siteTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Site> observable, Site oldValue, Site newValue) -> showSiteDetails(newValue));
        
    }    
    
    public void setMainApp(MainApp mainApp){
        this.mainApp=mainApp;
        
        siteTable.setItems(mainApp.getSites());      
 //       labelTest.textProperty().bindBidirectional(mainApp.timeProperty());
    }
    private void showSiteDetails(Site site) {
    	if (site != null) {
            addressLabel.setText(site.getName());
            statusLabel.setText(site.getStatus());
            pagesList.setItems(site.pagesProperty());
            if(site.getChange())
                changesLabel.setText("Да");
            else {
                changesLabel.setText("Нет");
            }
            
    	} else {
            addressLabel.setText("");
            changesLabel.setText("");
            statusLabel.setText("");
            pagesList.setItems(null);
    	}
    }
    
    @FXML
    private void handleDeletePerson() {
            int selectedIndex = siteTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                    siteTable.getItems().remove(selectedIndex);
                    mainApp.getService().setSites(mainApp.getUrl(mainApp.getSites()));
                    mainApp.getSites().forEach(h->System.out.println(h.getName()));
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
    private void handleNewPerson() {
            Site temp = new Site();
            boolean okClicked = mainApp.showSiteEditDialog(temp);
            if (okClicked) {
                    mainApp.getSites().add(temp);
                    mainApp.getSites().forEach(f->System.out.println(f.getName()));
                    mainApp.getService().getSites().add(temp.getName());
            }
    }  
    
    @FXML
    private void handleEditPerson() {
            Site selectedItem = siteTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                    boolean okClicked = mainApp.showSiteEditDialog(selectedItem);
                    if (okClicked) {
                            showSiteDetails(selectedItem);
                    }

            } else {
                    // Nothing selected.
                    Dialogs.create()
                            .title("No Selection")
                            .masthead("No Person Selected")
                            .message("Please select a person in the table.")
                            .showWarning();
            }
    }
}
