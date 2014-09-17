/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author dasha
 */
//@XmlRootElement(name = "root")
public class Site {
    private StringProperty name;
    private StringProperty status;
    private BooleanProperty change;
    private ObservableList<String> pages = FXCollections.<String>observableArrayList();
    
    public final void setName(String value) {
        nameProperty().set(value);
    }

    public final String getName() {
        return nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }

    public final void setStatus(String value) {
        statusProperty().set(value);
    }

    public final String getStatus() {
        return statusProperty().get();
    }

    public StringProperty statusProperty() {
        if (status == null) {
            status = new SimpleStringProperty();
        }
        return status;
    }

    public final void setChange(Boolean value) {
        changeProperty().set(value);
    }

    public final Boolean getChange() {
        return changeProperty().get();
    }

    public BooleanProperty changeProperty() {
        if (change == null) {
            change = new SimpleBooleanProperty();
        }
        return change;
    }
    public final void setPages(ObservableList<String> pages) {
        this.pages=pages;
    }
    public ObservableList<String> pagesProperty(){
        return pages;
    }
   //@XmlElementWrapper(name = "stateList")  
   //@XmlElement(name = "state")  
    public final ObservableList<String> getPages() {
        return pages;
    }

    public Site() {
        setName("");
        setStatus(null);
        setChange(false);
    }
    
    public Site(String name, Boolean change) {
        setName(name);
        setStatus(null);
        setChange(change);
    }
    
}
