package nl.b3p.batchloader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Meine Toonen <meinetoonen@b3partners.nl>
 */
public class LoaderTest {

    private static final Log log = LogFactory.getLog(nl.b3p.batchloader.Loader.class);
    private static List<String> lines = new ArrayList<>();
    
    public LoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
        String filename = "geoservices2.sql";
          BufferedReader br = null;
        log.info("Parsing file "+ filename);
        try {
            br = new BufferedReader(getResourceReader());
            String line = null;
            while( (line = br.readLine()) != null){
                log.info("Processing line: " + line);
                lines.add(line);
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

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testgetValue(){
        String firstProtocol = "wms";
        String firstName = "View Service PDOK";
        String firstURL =  "http://geodata.nationaalgeoregister.nl/bagviewer/wms?SERVICE=WMS&";
        String firstTilingProtocol = "null";

        String line = lines.get(0);
        Loader l = new Loader(null);
        String actualProtocol = l.getValue(0, line);
        String actualName = l.getValue(5, line);
        String actualURL = l.getValue(7, line);
        String actualTilingProtocol = l.getValue(10, line);

        Assert.assertEquals(firstProtocol, actualProtocol);
        Assert.assertEquals(firstName, actualName);
        Assert.assertEquals(firstURL, actualURL);
        Assert.assertEquals(firstTilingProtocol, actualTilingProtocol);
    }

    private static Reader getResourceReader() throws FileNotFoundException, URISyntaxException {
        URL url = LoaderTest.class.getResource("geoservices.sql");
        URI uri = url.toURI();
        FileReader fr = new FileReader(new File(uri));
        return fr;
    }
}
