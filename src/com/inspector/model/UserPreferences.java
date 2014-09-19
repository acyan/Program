package com.inspector.model;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
 
public class UserPreferences 
{
    private Preferences userPrefs;
    private StringProperty statusFrequency;
    private int changeFrequency;
    
    private final static String STATUS = "status";
    private final static String CHANGE = "change";
    
    public UserPreferences()
    {
        userPrefs = Preferences.userRoot().node(this.getClass().getName());
        statusFrequencyProperty().set(userPrefs.get(STATUS, "10000"));
        
    }

    public Preferences getUserPrefs() {
        return userPrefs;
    }

    public void setUserPrefs(Preferences userPrefs) {
        this.userPrefs = userPrefs;
    }

    public String getStatusFrequency() {
        return statusFrequencyProperty().get();
    }

    public void setStatusFrequency(String statusFrequency) {
        userPrefs.put(STATUS, statusFrequency);
        statusFrequencyProperty().set(statusFrequency);
    }

    public StringProperty statusFrequencyProperty() {
        if (statusFrequency == null) {
            statusFrequency = new SimpleStringProperty();
        }
        return statusFrequency;
    }
}