/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector;

import com.inspector.model.MyWrapperForList;
import com.inspector.model.Site;
import com.inspector.model.SiteListWrapper;
import com.inspector.views.RootViewController;
import com.inspector.views.SiteEditDialogController;
import com.inspector.views.SiteOverviewController;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.controlsfx.dialog.Dialogs;

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
        Site newSite = new Site("http://yandex.ru", Boolean.FALSE);
        newSite.pagesProperty().add("http://maps.yandex.ru");
        newSite.pagesProperty().add("http://market.yandex.ru");
        siteData.add(new Site("http://google.com", Boolean.TRUE));
        siteData.add(newSite);
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

    // Try to load last opened person file.
//    File file = getFilePath();
//    if (file != null) {
//        loadDataFromFile(file);
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
    
    public void loadDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(SiteListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            SiteListWrapper wrapper = (SiteListWrapper) um.unmarshal(file);

            siteData.clear();
            siteData.addAll(wrapper.getSites());

            // Save the file path to the registry.
            setFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Dialogs.create()
                    .title("Error")
                    .masthead("Could not load data from file:\n" + file.getPath())
                    .showException(e);
        }
    }

    public void saveDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(MyWrapperForList.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            SiteListWrapper wrapper = new SiteListWrapper();
            wrapper.setSites(siteData);
            
            MyWrapperForList<Site> ww = new MyWrapperForList<Site>();
            // Marshalling and saving XML to the file.
            m.marshal(ww, file);

            // Save the file path to the registry.
            setFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Dialogs.create().title("Error")
                    .masthead("Could not save data to file:\n" + file.getPath())
                    .showException(e);
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
    public static void main(String[] args) {
        launch(args);
    }
}
