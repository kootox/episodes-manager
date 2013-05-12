package org.kootox.episodesmanager.android;

import android.util.Log;
import org.kootox.episodesmanager.android.utility.EpisodesManagerException;
import org.kootox.episodesmanager.android.utility.URLResolver;
import org.kootox.episodesmanager.android.utility.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: couteau
 * Date: 7 avr. 2010
 */
public class TVRageAPI{

    private static String TAG = "EpisodesManager";

    private Map<String,Integer> lastResults;
    
    private DatabaseHelper dbHelper;

    public void init(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public void search(String search){

        lastResults = new HashMap<String, Integer>();

        Document xmlResults = null;

        try {
            xmlResults = URLResolver.readURL("http://services.tvrage.com/feeds/search.php?show=" + URLEncoder.encode(search,"UTF-8"));
        } catch (Exception eee) {
            Log.d("EpisodesManager", "error occurred", eee);
        }

        if (xmlResults == null) {
            return;
        }

        // normalize text representation
        xmlResults.getDocumentElement().normalize();


        NodeList listOfShows = xmlResults.getElementsByTagName("show");
        int totalShows = listOfShows.getLength();
        Log.d(TAG, "Search on tvrage returned " + totalShows + " shows");

        for (int s = 0; s < listOfShows.getLength(); s++) {
            Node firstShowNode = listOfShows.item(s);
            if (firstShowNode.getNodeType() == Node.ELEMENT_NODE) {

                Element firstShowElement = (Element) firstShowNode;

                //-------
                NodeList idList = firstShowElement.getElementsByTagName("showid");
                Element idElement = (Element) idList.item(0);

                NodeList textIdList = idElement.getChildNodes();
                Integer id = Integer.parseInt(textIdList.item(0).getNodeValue().trim());

                //-------
                NodeList nameList = firstShowElement.getElementsByTagName("name");
                Element nameElement = (Element) nameList.item(0);

                NodeList textNameList = nameElement.getChildNodes();
                String title = textNameList.item(0).getNodeValue().trim();

                Log.d(TAG,"Found " + title + " (" + id + ")");
                lastResults.put(title, id);
            }//end of if clause
        }//end of for loop with s var
    }

    public String[] getLastResults(){
        return lastResults.keySet().toArray(new String[1]);
    }

    public int getIdForTitle(String title){
        return lastResults.get(title);
    }

    public void add(String title){

        int id = getIdForTitle(title);

        Document xmlResults = URLResolver.readURL("http://services.tvrage.com/feeds/full_show_info.php?sid=" + id);

        if (xmlResults == null) {
            return;
        }

        //Create show
        Log.d(TAG, "Adding show " + title);

        try {
            dbHelper.storeShow(null, title, id);
        } catch (EpisodesManagerException eee) {
            Log.d(TAG, "An error occurred while trying to save show " + title);
        }

        //Get the seasons
        NodeList listOfSeasons = xmlResults.getElementsByTagName("Season");
        int totalSeasons = listOfSeasons.getLength();
        Log.d(TAG, "Trying to add " + totalSeasons + " seasons");

        for (int s = 0; s < listOfSeasons.getLength(); s++) {
            Node seasonNode = listOfSeasons.item(s);
            updateSeason(title, seasonNode);
        }//end of for loop with s var
    }


    /**
     * Update (or create if episode do not exist) episode from xml episode node
     *
     * @param seasonNumber      the season that 'owns' the episode's number
     * @param episodeNode the xml episode node
     * @param showTitle the episode's show's title
     */
    private void updateEpisode(String showTitle, int seasonNumber, Node episodeNode) {

        if (episodeNode.getNodeType() == Node.ELEMENT_NODE) {

            Element episodeElement = (Element) episodeNode;

            Integer number = XMLParser.getEpisodeNumber(episodeElement);
            Date airingDate = XMLParser.getEpisodeAiringDate(episodeElement);
            String title = XMLParser.getEpisodeTitle(episodeElement);

            try {
            dbHelper.storeEpisode(null, title, airingDate, false, false, number,
                    seasonNumber, showTitle);
            } catch (EpisodesManagerException eee) {
                Log.e(TAG, "An error occurred while saving episode : " + title);
            }

        }//end of if clause
    }

    /**
     * Update (or create if do not exist) season from the xml season node
     *
     * @param title       the show that 'owns' the season's title
     * @param seasonNode the season xml node
     */
    private void updateSeason(String title, Node seasonNode) {

        if (seasonNode.getNodeType() == Node.ELEMENT_NODE) {

            Element firstSeasonElement = (Element) seasonNode;
            Integer seasonNumber = Integer.parseInt(firstSeasonElement.getAttribute("no"));

            NodeList episodesList = firstSeasonElement.getElementsByTagName("episode");
            int totalEpisodes = episodesList.getLength();

            for (int e = 0; e < totalEpisodes; e++) {
                Node episodeNode = episodesList.item(e);
                updateEpisode(title, seasonNumber, episodeNode);
            }//end of for loop with e var
        }//end of if clause
    }

}
