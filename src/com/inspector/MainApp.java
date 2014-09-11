/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector;

import com.inspector.model.Site;
import com.inspector.views.SiteOverviewController;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author dasha
 */
public class MainApp extends Application{

    private Stage primaryStage;
    private BorderPane rootLayout;

    
    private ObservableList<Site> siteData = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Приложение");   
        
        initRootLayout();
        showSiteOverview();
    }

    public MainApp() {
        siteData.add(new Site("http://google.com", Boolean.TRUE));
        siteData.add(new Site("http://yandex.ru", Boolean.FALSE));
    }
    
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/RootView.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void showSiteOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/SiteOverview.fxml"));
            AnchorPane siteOverview = (AnchorPane) loader.load();
            
            rootLayout.setCenter(siteOverview);
            
            SiteOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Stage getPrimaryStage() {
            return primaryStage;
    }
    public ObservableList<Site> getSites(){
        return siteData;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
