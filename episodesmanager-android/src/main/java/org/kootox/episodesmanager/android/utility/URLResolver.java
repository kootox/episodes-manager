package org.kootox.episodesmanager.android.utility;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * User: couteau
 * Date: 7 avr. 2010
 */
public class URLResolver {

    private static String TAG = "EpisodesManager";

    /**
     * Get the document located at the URL url
     * @param url the url to download
     * @return the downloaded Document
     */
    public static Document readURL(String url){

        //initiate the connection
        Document doc = null;
        try {

            InputStream in = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = httpclient.execute(new HttpGet(url));

                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG,"Network exception", e);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(in);

            in.close();

        } catch (UnknownHostException uhe){
            Log.e(TAG,"Cannot connect to tvrage, check your internet connection");
        } catch (MalformedURLException mue){
            Log.e(TAG,"An error occured trying to contact TVRage Website", mue);
        } catch (IOException ioe){
            Log.e(TAG,"An error occured trying to contact TVRage Website", ioe);
        } catch (ParserConfigurationException pce){
            Log.e(TAG,"Error in configuration", pce);
        } catch (SAXException saxe){
            Log.e(TAG,"Error in TVRage response", saxe);
        }

        return doc;
    }
}
