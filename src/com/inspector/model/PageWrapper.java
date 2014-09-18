/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Даша
 */
public class PageWrapper {
    private String name;
    private String status;

    public PageWrapper(String name, String status) {
        this.name = name;
        this.status = status;
    }
   
    public Page getPage(){
        Page page = new Page(this.name);
        return page;
    }
   
}
