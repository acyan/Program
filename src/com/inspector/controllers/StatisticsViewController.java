/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.controllers;

import com.inspector.util.DBAdapter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author dasha
 */
public class StatisticsViewController {
    @FXML
    private BarChart<String, Integer> chart;

    @FXML
    private CategoryAxis xAxis;

    private ObservableList<String> pages = FXCollections.observableArrayList();
    private DBAdapter adapter;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // TODO

    }    
   
    public void setData(List<String> names, DBAdapter adapter){
        
        this.adapter = adapter;
        pages = FXCollections.observableArrayList(names);
        xAxis.setCategories(pages);
        
        int[] counter = new int[pages.size()];
        for(int i=0;i<pages.size();i++){
            int count = adapter.getCount(pages.get(i))-1;
            counter[i]=count;
        }
        
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        
        for (int i = 0; i < counter.length; i++) {
        	series.getData().add(new XYChart.Data<>(pages.get(i), counter[i]));
        }
        
        chart.getData().add(series);
    }
}
