/*
 * Copyright (C) 2009 Jean Couteau
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.kootox.episodesmanager.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;


/**
 *
 * @author couteau
 */
public class WebHelper {
    
    /** Logger */
    private final static Log log = LogFactory.getLog(WebHelper.class);
    
    /**
     * Get the document located at the URL url
     * @param url the url to download
     * @return the downloaded Document
     */
    public static Document readURL(String url){

        if (log.isDebugEnabled()) {
            log.debug("Fetch document from : " + url);
        }

        //initiate the connection
        Document doc = null;
        InputStream in = null;
        try {
            URL distantURL = new URL(url);

            URLConnection conn = distantURL.openConnection();

            conn.setConnectTimeout(10000);

            in = conn.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(in);

        } catch (UnknownHostException uhe){
            log.error("Cannot connect to website, check your internet connection");
        } catch (MalformedURLException mue){
            log.error("An error occured trying to contact website : " + mue.getMessage());
        } catch (IOException ioe){
            log.error("An error occured trying to contact website : " + ioe.getMessage());
        } catch (ParserConfigurationException pce){
            log.error("Error in configuration", pce);
        } catch (SAXException saxe){
            log.error("Error in response", saxe);
        } finally {
            if (in != null) {
                try {
                    in.close();

                } catch (IOException eee) {
                    log.debug("Could not close stream", eee);
                }
            }
        }

        return doc;
    }

    /**
     *
     * Download a file from the internet
     *
     * @param destinationDirectory the directory where the file will be saved
     * @param destinationFileName the name under which the file is saved
     * @param source the url from which to download the file
     */
    public static void downloadFile(File destinationDirectory,
                                    String destinationFileName, URL source) {

        FileOutputStream out = null;
        InputStream in = null;

        try {
            URLConnection conn = source.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "binary/data");
            in = conn.getInputStream();

            File destinationFile = new File(destinationDirectory, destinationFileName);
            out = new FileOutputStream(destinationFile);

            byte[] b = new byte[1024];
            int count;

            while ((count = in.read(b)) >= 0) {
                out.write(b, 0, count);
            }

        } catch (IOException eee) {
            log.error("Could not download file ", eee);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException eee) {
                log.debug("Could not close stream", eee);
            }
        }

    }
    
}
