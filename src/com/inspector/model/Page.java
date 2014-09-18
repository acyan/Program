/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Даша
 */
public class Page {
    private StringProperty name;
    private StringProperty status;
    
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

    public Page(String name) {
        setName(name);
        setStatus(null);
    }
    
    
}
