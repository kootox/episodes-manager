/*
 * #%L
 * episodesmanager-services
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2009 - 2010 Jean Couteau
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.kootox.episodesmanager.services.importExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.services.databases.XMLParser;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Date;

/**
 * User: couteau
 * Date: 12 mars 2010
 */
public class XMLReader implements EpisodesManagerService {

    /** Logger */
    private final static Log log = LogFactory.getLog(XMLReader.class);

    protected ServiceContext serviceContext;

    public XMLReader(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Get the document located at the URL url
     * @param file The file to read
     * @return the downloaded Document
     */
    private Document readFile(File file){

        //initiate the connection
        Document doc = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(in);

            in.close();

        } catch (FileNotFoundException eee){
            log.error("An error occurred", eee);
        } catch (ParserConfigurationException eee){
            log.error("An error occurred", eee);
        } catch (IOException eee){
            log.error("An error occurred", eee);
        } catch (SAXException eee){
            log.error("An error occurred", eee);
        } finally {
            try {
                in.close();
            } catch (IOException eee) {
                log.error("Error trying to close file");
            } catch (NullPointerException eee) {
                log.debug("Stream was not existing");
            }
        }

        return doc;
    }

    /**
     * Import a database from XML
     * @param file The file to read from
     */
    public void readFromXML(File file){

        ShowsService service = serviceContext.newService(ShowsService.class);

        Document xmlResults = readFile(file);

        // normalize text representation
        xmlResults.getDocumentElement().normalize ();


        NodeList listOfShows = xmlResults.getElementsByTagName("show");
        int totalShows = listOfShows.getLength();

        for(int s=0; s<totalShows ; s++){
            Node firstShowNode = listOfShows.item(s);
            if(firstShowNode.getNodeType() == Node.ELEMENT_NODE){

                Element showElement = (Element)firstShowNode;

                //get show info
                String showName = XMLParser.getStringValue(showElement, "name");
                Boolean showStatus = XMLParser.getBooleanValue(showElement, "status", "Canceled/Ended");
                String showNetwork = XMLParser.getStringValue(showElement, "network");
                Date showAirtime = XMLParser.getDateValue(showElement, "airtime", new SimpleDateFormat("HH:MM"));
                String showTimezone = XMLParser.getStringValue(showElement, "timezone");
                Integer showRuntime = XMLParser.getIntegerValue(showElement, "runtime");
                String showOriginCountry = XMLParser.getStringValue(showElement, "origin_country");
                Integer showId = XMLParser.getIntegerValue(showElement, "tvRageId");

                Show show;

                try {
                    show = service.createShow(showName);
                } catch (AlreadyExistException eee) {
                    show = service.getShowByName(showName);
                }

                show.setAirtime(showAirtime);
                show.setNetwork(showNetwork);
                show.setOriginCountry(showOriginCountry);
                show.setOver(showStatus);
                show.setRuntime(showRuntime);
                show.setTimeZone(showTimezone);
                show.setTvrageId(showId);

                service.updateShow(show);



                //Get the seasons
                NodeList listOfSeasons = xmlResults.getElementsByTagName("season");
                int totalSeasons = listOfSeasons.getLength();

                for(int se=0; se<totalSeasons ; se++){
                    Node seasonNode = listOfSeasons.item(se);
                    updateSeason(show, seasonNode);

                }//end of for loop with se var

            }//end of if clause
        }//end of for loop with s var

    }


    /**
     * Update (or create if do not exist) season from the xml season node
     * @param show the show that 'owns' the season to be updated
     * @param seasonNode the season xml node
     */
    private void updateSeason(Show show, Node seasonNode){

        SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);

        if(seasonNode.getNodeType() == Node.ELEMENT_NODE){

            Element firstSeasonElement = (Element)seasonNode;
            Integer seasonNumber = XMLParser.getIntegerValue(firstSeasonElement, "number");

            Season season;

            //If season exists get it else create it
            if (!seasonsService.seasonExists(show, seasonNumber)){
                try {
                    season = seasonsService.createSeason(show, seasonNumber);
                    season.setNumber(seasonNumber);
                    seasonsService.updateSeason(season);
                } catch (Exception te) {
                    log.error("An error occurred : ", te);
                    return;
                }
            } else {
                season = seasonsService.getSeasonByNumber(show, seasonNumber);
            }

            NodeList episodesList = firstSeasonElement.getElementsByTagName("episode");
            int totalEpisodes = episodesList.getLength();

            for(int e=0; e<totalEpisodes ; e++){
                Node episodeNode = episodesList.item(e);
                updateEpisode(season, episodeNode);
            }//end of for loop with e var
        }//end of if clause
    }


        /**
     * Update (or create if episode do not exist) episode from xml episode node
     * @param season the season that 'owns' the episode
     * @param episodeNode the xml episode node
     */
    private void updateEpisode(Season season, Node episodeNode) {

        EpisodesService episodesService = serviceContext.newService(EpisodesService.class);

        if(episodeNode.getNodeType() == Node.ELEMENT_NODE){

            Element episodeElement = (Element)episodeNode;

            Integer number = XMLParser.getIntegerValue(episodeElement,"number");
            Date airingDate = XMLParser.getDateValue(episodeElement, "airdate", new SimpleDateFormat("yyyy-MM-dd"));
            String title = XMLParser.getStringValue(episodeElement, "title");
            Boolean acquired = XMLParser.getBooleanValue(episodeElement, "acquired");
            Boolean watched = XMLParser.getBooleanValue(episodeElement, "watched");

            Episode episode = null;

            if (!episodesService.episodeExistsByNumber(season, number)){
                try {
                    episode = episodesService.createEpisode(season, number, title);
                } catch (Exception eee){
                    log.error("An error occurred : ", eee);
                }
            } else {
                try {
                    episode = episodesService.getEpisodeByNumber(season, number);
                } catch (Exception eee){
                    log.error("An error occurred : ", eee);
                }
            }

            if (null!=episode){
                episode.setAiringDate(airingDate);
                episode.setTitle(title);
                episode.setAcquired(acquired);
                episode.setViewed(watched);
                episodesService.updateEpisode(episode);
            }
        }//end of if clause
    }
}
