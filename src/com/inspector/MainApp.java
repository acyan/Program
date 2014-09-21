/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector;

import com.inspector.model.ChangesService;
import com.inspector.model.FileUtil;
import com.inspector.model.Message;
import com.inspector.model.Page;
import com.inspector.model.Site;
import com.inspector.model.SiteWrapper;
import com.inspector.model.Status;
import com.inspector.model.StatusService;
import com.inspector.model.UserPreferences;
import com.inspector.views.MessagesViewController;
import com.inspector.views.RootViewController;
import com.inspector.views.SettingsViewController;
import com.inspector.views.SiteEditDialogController;
import com.inspector.views.SiteOverviewController;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.DurationConverter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.prefs.Preferences;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
    private TabPane tabPane;

    
    private ObservableList<Site> siteData = FXCollections.observableArrayList();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    
    private StatusService statusService;
    private UserPreferences pref;
    private ChangesService changesService;
 //   private SimpleStringProperty time;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Приложение");   
        this.primaryStage.setOnCloseRequest((WindowEvent event) -> {
            saveData();
        });
        
        initRootLayout();
        showSiteOverview();
        showMessagesView();
    }

    public MainApp() {
        Site newSite = new Site("http://yandex.ru", Boolean.TRUE);
        newSite.pagesProperty().add(new Page("http://yandex.ru"));
        newSite.pagesProperty().add(new Page("http://market.yandex.ru"));
      //  siteData.add(new Site("http://google.com", Boolean.TRUE));
        siteData.add(newSite);
        loadData();
        
     //   siteData.forEach(site->addFolders(site));
        
        pref=new UserPreferences();
        pref.statusFrequencyProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(Integer.parseInt(newValue)>0){
                
                statusService.setPeriod(new Duration(Integer.parseInt(newValue)));  
                if(!statusService.isRunning()){
                    if(statusService.getState()==Worker.State.CANCELLED)
                        statusService.restart();
                    else
                        statusService.start();
                    
                     System.out.println("сервис перезапущен");
                }
            } else{
                statusService.cancel();
                System.out.println("сервис завершен");
            }

        });
        
        this.statusService = new StatusService(getUrl(siteData));
        statusService.setDelay(new Duration(300));
        statusService.setPeriod(new Duration(Integer.parseInt(pref.getStatusFrequency())));  

     //   statusService.setPeriod(new Duration(10000)); 
        statusService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        BlockingQueue<String> results = null;
            @Override
            public void handle(WorkerStateEvent event) {
                results = (BlockingQueue<String>) event.getSource().getValue();
                siteData.forEach((site) -> {
                    if(!site.getStatus().equals(results.peek())){
                        if(results.peek().equals(Status.ACTIVE.getValue()))
                            addMessage("Сайт "+site.getName()+" доступен");
                        else {
                            addMessage("Сайт "+site.getName()+" не доступен");
                        }
                    }
                    site.setStatus(results.poll());
            });
                
            }
        });
        if(!statusService.getPeriod().lessThan(Duration.ONE))
            statusService.start();
        
        this.changesService = new ChangesService(getPages());
        changesService.setDelay(new Duration(3000));
        changesService.setPeriod(new Duration(5000));
        
        changesService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            BlockingQueue<String> results = null;
            @Override
            public void handle(WorkerStateEvent event) {
                results = (BlockingQueue<String>) event.getSource().getValue();
                siteData.forEach(site->{
                    site.getPages().forEach(page->{
                        if(page.getSum()==null){
                            page.setSum(results.poll());
                        } else if(!page.getSum().equals(results.peek())){
                            addMessage("Произошли изменения на странице "+page.getName());
                            page.setSum(results.poll());
                            page.setStatus("yes");
                        } else{
                            page.setStatus("no");
                        }
                    });
                });
            }
        });
        changesService.start();
//        time = new SimpleStringProperty("0");
//        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                int val = Integer.parseInt(time.getValue());
//                val++;
//                time.setValue(new Integer(val).toString());
//            }
//        }));
//        timer.setCycleCount(Timeline.INDEFINITE);
//        timer.play();       
    }
    
public void initRootLayout() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class
                .getResource("views/RootView.fxml"));
        rootLayout = (BorderPane) loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        RootViewController controller = loader.getController();
        controller.setMainApp(this);

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
            tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Tab sitesTab = new Tab("Сайты");
            sitesTab.setContent(siteOverview);
            tabPane.getTabs().addAll(sitesTab);
            
            rootLayout.setCenter(tabPane);
            
            SiteOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessagesView() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/MessagesView.fxml"));
            AnchorPane messagesView = (AnchorPane) loader.load();
            
            Tab messagesTab = new Tab("Уведомления");
            messagesTab.setContent(messagesView);
            tabPane.getTabs().add(messagesTab);
            
            MessagesViewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch(IOException e){
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
    
    public void addFolders(Site site){
        String name = site.getName().replace("http://", "").replaceAll("/", " ");
        try{
            (new File("sites/"+name)).mkdirs();           
            site.getPages().forEach(s->{
                String pageName = s.getName().replace("http://", "").replaceAll("/", " ");
                (new File("sites/"+name+"/"+pageName)).mkdirs();
                    });
           
        } catch(Exception e){
            
        }     
    }
    
    public void addMessage(String name){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        messages.add(new Message(name, time));
    }
    
    public Stage getPrimaryStage() {
            return primaryStage;
    }
    public ObservableList<Site> getSites(){
        return siteData;
    }
    
    public StatusService getService(){
        return statusService;
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
    
    public List<String> getPages(){
        List<String> result = new ArrayList<>();
        for(Site site:siteData){
            if(site.getChange()){
                for(Page page:site.getPages()){
                   result.add(page.getName());
               }               
            }

        }
        return result;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }
    
//    public SimpleStringProperty timeProperty() {
//        if (time == null) {
//            time = new SimpleStringProperty();
//        }
//        return time;
//    }    
    public static void main(String[] args) {
        launch(args);
    }
}
