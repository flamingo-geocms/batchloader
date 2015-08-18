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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen <meinetoonen@b3partners.nl>
 */
public class Loader {
    private URL url;
    private List<URL> urls = new ArrayList<>();
    private static final Log log = LogFactory.getLog(Loader.class);
    
    public Loader(URL url){
        this.url = url;
    }
    
    public void parse(String filename){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = null;
            while( (line = br.readLine()) != null){
                URL u = parseLine(line);
                if(u != null){
                    urls.add(u);
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
    }
    
    private URL parseLine(String line){
        String url = line;
        URL u = null;
        try {
            u=  new URL(url);
        } catch (MalformedURLException ex) {
            log.error("Cannot create url from :" + line + ". Was trying to parse a URL from line " + url);
        }
        return u;
    }
    
    public void start(){
        
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
}
