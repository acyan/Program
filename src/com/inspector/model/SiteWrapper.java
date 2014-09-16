/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import java.util.ArrayList;
import javafx.collections.FXCollections;

/**
 *
 * @author dasha
 */
public class SiteWrapper {
    private String name;
    private String status;
    private Boolean change;
    private ArrayList<String> pages;

    public SiteWrapper(Site site) {
        this.name = site.getName();
        this.status = site.getStatus();
        this.change = site.getChange();
        this.pages = new ArrayList<>(site.getPages());
    }

    public Site getSite(){
        Site site = new Site(this.name, this.change);
        site.setPages(FXCollections.observableArrayList(this.pages));
        return site;
    }

    
    
}
