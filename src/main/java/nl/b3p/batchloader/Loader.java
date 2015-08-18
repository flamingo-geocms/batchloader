/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.b3p.batchloader;

/**
 *
 * @author Meine Toonen <meinetoonen@b3partners.nl>
 */
public class Loader {
    
    public Loader(){
        
    }
    
    public void parse(String filename){
        
    }
    
    public void start(){
        
    }

    public static void main (String[] args){
        String filename = args[0];
        Loader l = new Loader();
        l.parse(filename);
        l.start();
    }
}
