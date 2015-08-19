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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen <meinetoonen@b3partners.nl>
 */
public class Loader {
    
    private final int POS_NAME = 5;
    private final int POS_PROTOCOL = 0;
    private final int POS_URL = 7;
    private final int POS_TILINGPROTOCOL = 10;

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
        String protocol = getValue(POS_PROTOCOL, line);
        String name = getValue(POS_NAME,line);
        String url = getValue(POS_URL,line);
        String tilingProtocol = getValue(POS_TILINGPROTOCOL,line);

        log.info("Naam: " + name);
        log.info("URL: " + url);
        try {
            URL u=  new URL(url);
            t = new Tuple();
            t.url = u;
            t.naam = name;
            t.protocol = protocol;
            t.tilingProtocol = tilingProtocol;
            t.line = line;
        } catch (MalformedURLException ex) {
            log.error("Cannot create url from :" + line + ". Was trying to parse a URL from line " + url);
        }
        return t;
    }

    public String getValue(int pos, String line){
        line = line.substring(line.indexOf("VALUES (") +8);
        String rest = line;
        String delimiter = ",";
        String found = null;
        for (int i = 0; i <= pos; i++) {
            int index = rest.indexOf(delimiter);
            found = rest.substring(0, index);
            rest = rest.substring(index+2);
        }
        found = found.replaceAll("'", "");

        return found;
    }
    
    public void start(){
        int category = 1;
        String request = url.toString() + "action/geoservice?add=true&category="+category;
        for (Tuple t : urls) {
            try {
                String r2 = request;
                String urlEncoded = URLEncoder.encode(t.url.toString(), "UTF-8");
                r2 += "&url=" + urlEncoded;
                r2 += "&serviceName=" + t.naam;
                r2 += "&protocol="+ t.protocol;
                r2 += "&crs=28992";
                r2 += "&serviceBbox=-285401.0,22598.0,595401.0,903401.0";
                r2 += "&tilingProtocol="+t.tilingProtocol;
                r2 += "&tileSize=256";
                r2 += "&resolutions=3440.64,1720.32,860.16,430.08,215.04,107.52,53.76,26.88,13.44,6.72,3.36,1.68,0.84,0.42,0.21,0.105";
                r2 += "&imageExtension=png";

                log.error("Request url: " + r2);
           
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
        private String tilingProtocol;
        private String line;
    }
}
