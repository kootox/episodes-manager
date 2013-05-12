package org.kootox.episodesmanager.android.utility;

import android.util.Log;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: couteau
 * Date: 16 mai 2010
 */
public class XMLParser {

    private static String TAG = "EpisodesManager";

    /**
     * Date format for all TVRage dates
     */
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Private default constructor to avoid instantiation *
     */
    private XMLParser() {
    }

    /**
     * Method that gets the show name element and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show name contained in the name tag
     */
    public static String getShowName(Element xmlResults) {

        NodeList nameList = xmlResults.getElementsByTagName("name");
        NodeList textNameList = nameList.item(0).getChildNodes();
        return StringEscapeUtils.unescapeHtml(textNameList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets the show airtime from the airtime tag and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show airtime contained in the airtime tag
     */
    public static Date getShowAirtime(Element xmlResults) {
        NodeList airtimeList = xmlResults.getElementsByTagName("airtime");
        NodeList textAirtimeList = airtimeList.item(0).getChildNodes();
        String showAirtime = textAirtimeList.item(0).getNodeValue().trim();
        Date airtime = null;
        try {
            DateFormat hourFormat = new SimpleDateFormat("HH:MM");
            airtime = hourFormat.parse(showAirtime);
        } catch (ParseException pe) {
            Log.d(TAG, "Could not parse airtime : " + showAirtime);
        }

        return airtime;
    }

    /**
     * Method that gets the show timezone from the timezone tag and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show timezone contained in the timezone tag
     */
    public static String getShowTimezone(Element xmlResults) {
        NodeList timezoneList = xmlResults.getElementsByTagName("timezone");
        NodeList textTimezoneList = timezoneList.item(0).getChildNodes();
        return StringEscapeUtils.unescapeHtml(textTimezoneList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets the show runtime from the runtime tag and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show runtime contained in the runtime tag
     */
    public static Integer getShowRuntime(Element xmlResults) {

        Integer returnInt = null;

        NodeList runtimeList = xmlResults.getElementsByTagName("runtime");
        NodeList textRuntimeList = runtimeList.item(0).getChildNodes();
        String showRuntime = textRuntimeList.item(0).getNodeValue().trim();

        if (!"null".equals(showRuntime)) {
            returnInt = Integer.parseInt(showRuntime);
        }
        return returnInt;
    }

    /**
     * Method that gets the show network from the network tag and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show network contained in the network tag
     */
    public static String getShowNetwork(Element xmlResults) {
        NodeList networkList = xmlResults.getElementsByTagName("network");
        NodeList textNetworkList = networkList.item(0).getChildNodes();
        return StringEscapeUtils.unescapeHtml(textNetworkList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets the show status from the status tag and returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show status contained in the status tag
     */
    public static Boolean getShowOver(Element xmlResults) {

        Boolean returnBoolean = null;

        NodeList statusList = xmlResults.getElementsByTagName("status");
        NodeList textStatusList = statusList.item(0).getChildNodes();
        String textStatus = textStatusList.item(0).getNodeValue().trim();

        if (!"null".equals(textStatus)) {
            returnBoolean = "Cancel/Ended".equals(textStatus);
        }

        return returnBoolean;
    }


    /**
     * Method that gets the show origin country from the origin_country tag and
     * returns it
     *
     * @param xmlResults the xml document to go through
     * @return the show origin country contained in the origin_country tag
     */
    public static String getShowOriginCountry(Element xmlResults) {
        NodeList originList = xmlResults.getElementsByTagName("origin_country");
        NodeList textOriginList = originList.item(0).getChildNodes();
        return StringEscapeUtils.unescapeHtml(textOriginList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets the episode title from the episode element title tag
     *
     * @param episodeElement the xml element to go through
     * @return the episode title contained in the title tag
     */
    public static String getEpisodeTitle(Element episodeElement) {
        NodeList titleList = episodeElement.getElementsByTagName("title");
        NodeList textTitleList = titleList.item(0).getChildNodes();
        return StringEscapeUtils.unescapeHtml(textTitleList.item(0).getNodeValue());
    }

    /**
     * Method that gets the episode number from the episode element seasonum tag
     *
     * @param episodeElement the xml element to go through
     * @return the episode number contained in the episode number tag
     */
    public static Integer getEpisodeNumber(Element episodeElement) {
        NodeList numberList = episodeElement.getElementsByTagName("seasonnum");
        Element numberElement = (Element) numberList.item(0);
        if (numberElement == null) {
            numberList = episodeElement.getElementsByTagName("number");
            numberElement = (Element) numberList.item(0);
        }
        NodeList textNumberList = numberElement.getChildNodes();
        return Integer.parseInt(textNumberList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets if the episode was acquired from the acquired tag
     *
     * @param episodeElement the xml element to go through
     * @return if the episode was acquired from the acquired tag
     */
    public static Boolean getEpisodeAcquired(Element episodeElement) {
        NodeList numberList = episodeElement.getElementsByTagName("acquired");
        NodeList textNumberList = numberList.item(0).getChildNodes();
        return Boolean.parseBoolean(textNumberList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets if the episode was watched from the watched tag
     *
     * @param episodeElement the xml element to go through
     * @return if the episode was watched from the watched tag
     */
    public static Boolean getEpisodeWatched(Element episodeElement) {
        NodeList numberList = episodeElement.getElementsByTagName("watched");
        NodeList textNumberList = numberList.item(0).getChildNodes();
        return Boolean.parseBoolean(textNumberList.item(0).getNodeValue().trim());
    }

    /**
     * Method that gets the episode airing date from the episode element airdate
     * tag
     *
     * @param episodeElement the xml element to go through
     * @return the episode airing date contained in the episode airdate tag
     */
    public static Date getEpisodeAiringDate(Element episodeElement) {

        Date returnDate;

        NodeList airingList = episodeElement.getElementsByTagName("airdate");
        NodeList textAiringList = airingList.item(0).getChildNodes();
        String date = textAiringList.item(0).getNodeValue();
        Date airing = null;
        Date airingNull = null;
        try {
            airingNull = format.parse("0000-00-00");
            airing = format.parse(date);
        } catch (ParseException pe) {
            Log.d(TAG, "Could not parse date : " + date);
        }


        if ((null != airing) && (!date.substring(5).equals("00-00"))) {
            returnDate = airing;
        } else {
            returnDate = airingNull;
        }

        return returnDate;
    }

    public static int getShowId(Element showElement) {
        NodeList numberList = showElement.getElementsByTagName("tvrageID");
        NodeList textNumberList = numberList.item(0).getChildNodes();
        return Integer.parseInt(textNumberList.item(0).getNodeValue().trim());
    }

    public static int getSeasonNumber(Element seasonElement) {
        NodeList numberList = seasonElement.getElementsByTagName("number");
        NodeList textNumberList = numberList.item(0).getChildNodes();
        return Integer.parseInt(textNumberList.item(0).getNodeValue().trim());
    }
}
