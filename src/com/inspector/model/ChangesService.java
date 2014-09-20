/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import com.sun.corba.se.spi.copyobject.CopierManager;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author dasha
 */
public class ChangesService extends ScheduledService<BlockingQueue>{

    private CopyOnWriteArrayList<Site> pages;
    
    public List<Site> getSites() {
        return pages;
    }

    public void setSites(List<Site> sites) {
        this.pages = new CopyOnWriteArrayList(sites);
    }
    
    public ChangesService(List<Site> sites) {
        this.pages = new CopyOnWriteArrayList(sites);
    }
    
    @Override
    protected Task<BlockingQueue> createTask() {
        final Task<BlockingQueue> task;
        task = new Task<BlockingQueue>() {
            BlockingQueue<Site> result;
            @Override
            protected BlockingQueue call() throws Exception {
                
                
                
                return result;
                
            }
        };
        return task;        
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
    
}
