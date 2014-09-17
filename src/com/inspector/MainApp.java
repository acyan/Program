/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector;

import com.inspector.model.FileUtil;
import com.inspector.model.MyService2;
import com.inspector.model.Site;
import com.inspector.model.SiteWrapper;
import com.inspector.model.UserPreferences;
import com.inspector.views.RootViewController;
import com.inspector.views.SettingsViewController;
import com.inspector.views.SiteEditDialogController;
import com.inspector.views.SiteOverviewController;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.DurationConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.converter.*;

/**
 *
 * @author dasha
 */
public class MainApp extends Application{

    private Stage primaryStage;
    private BorderPane rootLayout;

    
    private ObservableList<Site> siteData = FXCollections.observableArrayList();
    private MyService2 service;
    private UserPreferences pref;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Приложение");   
        this.primaryStage.setOnCloseRequest((WindowEvent event) -> {
            saveData();
        });
        initRootLayout();
        showSiteOverview();
    }

    public MainApp() {
        Site newSite = new Site("http://yandex.ru", Boolean.FALSE);
        newSite.pagesProperty().add("http://maps.yandex.ru");
        newSite.pagesProperty().add("http://market.yandex.ru");
        siteData.add(new Site("http://google.com", Boolean.TRUE));
        siteData.add(newSite);
        loadData();
        pref=new UserPreferences();
        pref.statusFrequencyProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(Integer.parseInt(newValue)>0){
                
                service.setPeriod(new Duration(Integer.parseInt(newValue)));  
                if(!service.isRunning()){
                    if(service.getState()==Worker.State.CANCELLED)
                        service.restart();
                    else
                        service.start();
                    
                     System.out.println("сервис перезапущен");
                }
            } else{
                service.cancel();
                System.out.println("сервис завершен");
            }

        });
        
        this.service = new MyService2(getUrl(siteData));
        service.setDelay(new Duration(300));
        service.setPeriod(new Duration(Integer.parseInt(pref.getStatusFrequency())));  
     //   service.setPeriod(new Duration(10000)); 
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                BlockingQueue<String> results = (BlockingQueue<String>) event.getSource().getValue();
                for(Site site:siteData){
                    site.setStatus(results.poll());
//                    pref.getUserPrefs().putInt("item", pref.getUserPrefs().getInt("item", 0)+1);
                }
             //   time.setI(0);
            }
        });
        if(!service.getPeriod().lessThan(Duration.ONE))
            service.start();
    }
    
public void initRootLayout() {
    try {
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class
                .getResource("views/RootView.fxml"));
        rootLayout = (BorderPane) loader.load();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        // Give the controller access to the main app.
        RootViewController controller = loader.getController();
        controller.setMainApp(this);

        primaryStage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }

    //loadData();
        // Try to load last opened person file.
    //    File file = getFilePath();
    //    if (file != null) {
    //        loadData(file);
    //    }
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

    
    public boolean showSiteEditDialog(Site site) {
            try {
                    // Load the fxml file and create a new stage for the popup dialog.
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(MainApp.class.getResource("views/SiteEditDialog.fxml"));
                    AnchorPane page = (AnchorPane) loader.load();

                    // Create the dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Редактировать");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);

                    // Set the person into the controller.
                    SiteEditDialogController controller = loader.getController();
                    controller.setDialogStage(dialogStage);
                    controller.setSite(site);

                    // Show the dialog and wait until the user closes it
                    dialogStage.showAndWait();

                    return controller.isOkClicked();
            } catch (IOException e) {
                    e.printStackTrace();
                    return false;
            }
    }
    public boolean showSettingsView(){
        try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("views/SettingsView.fxml"));
                AnchorPane page = (AnchorPane) loader.load();    

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Настройки");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);
                
                SettingsViewController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMainApp(this);
                
                dialogStage.showAndWait();
                return controller.isOkClicked();
                
            } catch (IOException e) {
                    e.printStackTrace();
                    return false;
            }
  
    }
    public void loadData() {
      XStream xstream = new XStream();
      xstream.alias("person", Site.class);

      try {
        String xml = FileUtil.readFile(new File("data.xml"));

        ArrayList<SiteWrapper> personList = (ArrayList<SiteWrapper>) xstream.fromXML(xml);

        siteData.clear();
        personList.forEach(f->{
            siteData.add(f.getSite());
        });

        siteData.forEach(file1-> {
            System.out.println(file1.getName()+" "+file1.getChange()+" ");
            file1.getPages().forEach(fil->System.out.println(fil));
        });

      } catch (Exception e) { // catches ANY exception

      }
    }



    public void saveData() {
      XStream xstream = new XStream();
      xstream.alias("person", Site.class);

      // Convert ObservableList to a normal ArrayList
      ArrayList<SiteWrapper> personList = new ArrayList<>();
      siteData.forEach(f->{
          personList.add(new SiteWrapper(f));
      });
      
      String xml = xstream.toXML(personList);
      try {
        FileUtil.saveFile(xml, new File("data.xml"));


      } catch (Exception e) { // catches ANY exception

      }    
    }
    
    public File getFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
           // primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
         //   primaryStage.setTitle("AddressApp");
        }
    }
    public Stage getPrimaryStage() {
            return primaryStage;
    }
    public ObservableList<Site> getSites(){
        return siteData;
    }
    
    public MyService2 getService(){
        return service;
    }
    public UserPreferences getPreferences(){
        return pref;
    }
    public List<String> getUrl(ObservableList<Site> sites){
        List<String> result = new ArrayList<String>();
        for(Site site:sites){
            result.add(site.getName());
        }
        return result;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
