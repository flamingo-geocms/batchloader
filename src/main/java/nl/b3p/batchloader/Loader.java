/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.b3p.batchloader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen <meinetoonen@b3partners.nl>
 */
public class Loader {
    private URL url;
    private List<Tuple> urls = new ArrayList<>();
    private static final Log log = LogFactory.getLog(Loader.class);
    
    public Loader(URL url){
        this.url = url;
    }
    
    public void parse(String filename){
        BufferedReader br = null;
        log.info("Parsing file "+ filename);
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = null;
            while( (line = br.readLine()) != null){
                log.info("Processing line: " + line);
                Tuple t = parseLine(line);
                if(t != null){
                    urls.add(t);
                }
            }
        } catch (FileNotFoundException ex) {
            log.error("File with service url not found: ",ex);
        } catch (IOException ex) {
            log.error("Error reading file: ",ex);
        }finally{
            if(br != null){
                try {
                    br.close();
                } catch (IOException ex) {
                    log.error("Error closing reader:",ex);
                }
            }
        }   
        log.info("Finished parsing");
    }
    
    private Tuple parseLine(String line){
       
        Tuple t = null;
        String protocol = line.substring(1, line.indexOf("',"));
        
        String rest = line.substring(line.indexOf("',")+2);
        String name = rest.substring(2, rest.indexOf("',"));
        
        rest = rest.substring(rest.indexOf("',")+2);
        int index = rest.indexOf(", '")+2;
        String url = rest.substring(index+1, rest.indexOf("',", index));
        log.info("Naam: " + name);
        log.info("URL: " + url);
        try {
            URL u=  new URL(url);
            t = new Tuple();
            t.url = u;
            t.naam = name;
            t.protocol = protocol;
        } catch (MalformedURLException ex) {
            log.error("Cannot create url from :" + line + ". Was trying to parse a URL from line " + url);
        }
        return t;
    }
    
    public void start(){
        int category = 2;
        String request = url.toString() + "action/geoservice?add=true&category="+category;
        for (Tuple t : urls) {
            try {
                String r2 = request;
                String urlEncoded = URLEncoder.encode(t.url.toString(), "UTF-8");
                r2 += "&url=" + urlEncoded;
                r2 += "&protocol="+ t.protocol;
                log.debug("Request url: " + r2);
            } catch (UnsupportedEncodingException ex) {
                log.error("Cannot encode uri",ex);
            }
        }
        
    }

    public static void main (String[] args) throws MalformedURLException{
        if(args.length != 2){
            log.error("Incorrect number of arguments given: " + args.length);
            throw new IllegalArgumentException("Should supply 2 arguments: filename and url");
        }
        String filename = args[0];
        String url = args[1];
        
        Loader l = new Loader(new URL(url));
        l.parse(filename);
        l.start();
    }
    
    class Tuple{
        private URL url;
        private String protocol;
        private String naam;
        
        
    }
}
