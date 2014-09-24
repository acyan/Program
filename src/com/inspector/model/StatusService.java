/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.model;

import com.inspector.util.Status;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
/**
 *
 * @author dasha
 */
public class StatusService extends ScheduledService<BlockingQueue>{

    private CopyOnWriteArrayList<String> sites;

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = new CopyOnWriteArrayList(sites);
    }
    
    public StatusService(List<String> sites) {
        this.sites = new CopyOnWriteArrayList(sites);
    }

    @Override
    protected Task<BlockingQueue> createTask() {
        final Task<BlockingQueue> task;
        task = new Task<BlockingQueue>(){
        
            @Override
            protected BlockingQueue call() throws Exception {          
                BlockingQueue result = new LinkedBlockingQueue<String>();  
                
            //    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            //    cm.setMaxTotal(100);

             //   CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
                try{
                    ExecutorService executor = Executors.newFixedThreadPool(sites.size());
                    List<Future<String>> results = new ArrayList<Future<String>>();
                    for (int i = 0; i < sites.size(); i++) {
                   //    HttpGet httpget = new HttpGet(sites.get(i));
                       Callable worker = new MyCallable2(sites.get(i));
                       Future<String> res = executor.submit(worker);
                       results.add(res);
                      // String url = hostList[i];
                    //   Runnable worker = new MyRunnable(url);
                    //   executor.execute(worker);
                    //   executor.submit(null);

                   }
                   executor.shutdown();
                   
                   // Wait until all threads are finish
//                   while (!executor.isTerminated()) {
//
//                   }
                   for(Future<String> element:results){
                       result.add(element.get());
                   }
                   System.out.println("\nFinished all threads "+getPeriod());    
                   
                } finally{
               //     httpclient.close();
                }
                return result;
            }
            
        };
        return task;
    }

  

//public static class MyCallable implements Callable {
//        private final CloseableHttpClient httpClient;
//        private final HttpContext context;
//        private final HttpGet httpget;
//
//        public MyCallable(CloseableHttpClient httpClient, HttpGet httpget) {
//            this.httpClient = httpClient;
//            this.context = new BasicHttpContext();
//            this.httpget = httpget;
//        }
//
//        @Override
//        public String call() throws Exception {
//            String result="";
//            try {             
//                CloseableHttpResponse response = httpClient.execute(httpget, context);
//                try {
//
//                    result = Status.ACTIVE.getValue();
//
//                } finally {
//                    response.close();
//                }
//            } catch(HttpHostConnectException e){
//                result = Status.INACTIVE.getValue();
//            }
//            catch (Exception e) {
//                System.out.println(" - error: " + e);
//            }
//            return result;
//        }
//    
//}


    public static class MyCallable2 implements Callable{
        String url;

        public MyCallable2(String url) {
            this.url = url;
        }
        
        @Override
        public String call() throws Exception {
            String result="";
            int code = 200;
            try {
                URL siteURL = new URL(url);
                code = getCode(new URL(url));
                if (code == 200) {
                    result = Status.ACTIVE.getValue();
                } else
                {
                    result = Status.INACTIVE.getValue();
                }
            } catch (Exception e) {
                
            }
            return result;
        }
        private int getCode(URL site){
            HttpURLConnection connection = null;
            int code=0;
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.16.0.3", 3128));
            try{
                connection = (HttpURLConnection) site.openConnection();
                
                connection.setRequestMethod("HEAD");

                connection.setReadTimeout(10000);
                connection.connect();
                code = connection.getResponseCode();
                
                if(code==302){
                    code = getCode(new URL(connection.getHeaderField("Location")));
                }                
            } catch (Exception e){
                code  = 404;
            } finally{
                connection.disconnect();
            }
            return code;
        }
    }
//    static class GetThread extends Thread {
//
//        private final CloseableHttpClient httpClient;
//        private final HttpContext context;
//        private final HttpGet httpget;
//        private final int id;
//        
//        
//        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
//            this.httpClient = httpClient;
//            this.context = new BasicHttpContext();
//            this.httpget = httpget;
//            this.id = id;
//        }
//
//        /**
//         * Executes the GetMethod and prints some status information.
//         */
//        @Override
//        public void run() {
//            try {
//                
//             //   System.out.println(id + " - about to get something from " + httpget.getURI());
//                System.out.print(httpget.getURI()+" ");                
//                CloseableHttpResponse response = httpClient.execute(httpget, context);
//                try {
//                //    System.out.println(id + " - get executed");
//                    // get the response body as an array of bytes
//                  //  HttpEntity entity = response.getEntity();
//                    System.out.println("active");
//
//                } finally {
//                    response.close();
//                }
//            } catch(HttpHostConnectException e){
//                System.out.println("inactive");
//            }
//            catch (Exception e) {
//                System.out.println(id + " - error: " + e);
//            }
//        }
//
//    }

}
