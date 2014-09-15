/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Даша
 */
@XmlRootElement(name = "sites")
public class SiteListWrapper {
    private List<Site> sites;
    
    @XmlElement(name="site")
    public List<Site> getSites(){
        return sites;
    }
    
    public void setSites(List<Site> sites){
        this.sites=sites;
    }
}
