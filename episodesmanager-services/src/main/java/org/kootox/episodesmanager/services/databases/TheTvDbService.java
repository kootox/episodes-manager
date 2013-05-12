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
package org.kootox.episodesmanager.services.databases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.services.WebHelper;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * Service to get info from TheTvDb.com website.
 * 
 * Service is not stateless, mirror info is stored inside service.
 * 
 * Last update date is stored into config.
 *
 * @author couteau
 */
public class TheTvDbService implements EpisodesManagerService{
    
    //The Mirror tag in mirrors list
    protected static final String MIRROR = "Mirror";
    protected static final String MIRROR_PATH = "mirrorpath";
    protected static final String MIRROR_TYPEMASK = "typemask";
    protected static final int MIRROR_TYPEMASK_XML = 1;
    protected static final int MIRROR_TYPEMASK_BANNER = 2;
    protected static final int MIRROR_TYPEMASK_ZIP = 4;
    //The id tag in mirrors list
    protected static final String MIRROR_ID = "id";

    protected static final String URL_SEARCH_SHOW = "http://www.thetvdb.com/api/GetSeries.php?seriesname=%s&language=%s";
    private static final String URL_DOWNLOAD_BANNER = "%s/%s";
    protected static final String URL_DOWNLOAD_SHOW = "%s/api/%s/series/%s/all/%s.zip";
    protected static final String URL_DOWNLOAD_UPDATES = "http://www.thetvdb.com/api/Updates.php?type=all&time=%s";

    //The Search Show XML attributes
    protected static final String SHOW = "Series";
    protected static final String SHOW_NAME = "SeriesName";
    protected static final String SHOW_ID = "seriesid";

    protected static final String SHOW_LANGUAGE = "language";


    //temp zip file name
    protected static final String TEMP_ZIP_FILENAME = "em_tmp.zip";

    /** Logger **/
    private final static Log log = LogFactory.getLog(TheTvDbService.class);

    /** Mirrors **/
    protected List<Mirror> xmlMirrors = new ArrayList<Mirror>();
    protected List<Mirror> bannerMirrors = new ArrayList<Mirror>();
    protected List<Mirror> zipMirrors = new ArrayList<Mirror>();

    /** The Series XML attributes **/
    protected static final String SERIES_NAME = "SeriesName";
    protected static final String SERIES_CONTENT_RATING = "ContentRating";
    protected static final String SERIES_FIRST_AIRED = "FirstAired";
    protected static final String SERIES_GENRE = "Genre";
    protected static final String SERIES_IMDB_ID = "IMDB_ID";
    protected static final String SERIES_ZAP2IT_ID = "zap2it_id";
    protected static final String SERIES_NETWORK = "Network";
    protected static final String SERIES_OVERVIEW = "Overview";
    protected static final String SERIES_STATUS = "Status";
    protected static final String SERIES_LAST_UPDATED = "lastupdated";
    protected static final String SERIES_RUNTIME = "Runtime";
    protected static final String SERIES_ACTORS = "Actors";

    /** The Episode XML attributes **/
    protected static final String EPISODE_NAME = "EpisodeName";
    protected static final String EPISODE_SEASON = "SeasonNumber";
    protected static final String EPISODE_NUMBER = "EpisodeNumber";
    protected static final String EPISODE_FIRST_AIRED = "FirstAired";
    protected static final String EPISODE_DIRECTOR = "Director";
    private static final String EPISODE_SUMMARY = "Overview";
    private static final String EPISODE_IMDB_ID = "IMDB_ID";
    private static final String EPISODE_WRITER = "Writer";
    private static final String EPISODE_LAST_UPDATED = "lastupdated";
    private static final String EPISODE_GUEST_STARS = "GuestStars";
    private static final String EPISODE_THETVDB_ID = "id";
    private static final String SERIES_BANNER = "banner";
    private static final String UPDATE_SHOWS = "Series";
    private static final String UPDATE_TIME = "Time";

    protected ServiceContext serviceContext;

    public TheTvDbService() {
    }

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        updateMirrorsList();
    }

    public void updateMirrorsList() {        

        if (log.isInfoEnabled()) {
            log.info("Update mirrors list");
        }
        
        xmlMirrors.clear();
        bannerMirrors.clear();
        zipMirrors.clear();

        EpisodesManagerConfig config = serviceContext.getEpisodesManagerConfig();

        //Get back XML Document
        Document xmlResults = WebHelper.readURL("http://www.thetvdb.com/api/" +
                config.getTheTvDbApiKey() + "/mirrors.xml");

        if (xmlResults != null) {
            // normalize text representation
            xmlResults.getDocumentElement().normalize();


            NodeList listOfMirrors = xmlResults.getElementsByTagName(MIRROR);
            int totalMirrors = listOfMirrors.getLength();
            if (log.isDebugEnabled()) {
                log.debug("Found " + totalMirrors + " mirrors");
            }

            //iterate through mirrors
            for (int s = 0; s < listOfMirrors.getLength(); s++) {
                Node firstShowNode = listOfMirrors.item(s);
                if (firstShowNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstShowElement = (Element) firstShowNode;

                    //------- Get Mirror id
                    NodeList idList = firstShowElement.getElementsByTagName(
                        MIRROR_ID);
                    Element idElement = (Element) idList.item(0);

                    NodeList textIdList = idElement.getChildNodes();
                    Integer id = Integer.parseInt(
                        textIdList.item(0).getNodeValue().trim());

                    //------- Get Mirror path
                    NodeList pathList = firstShowElement.getElementsByTagName(
                        MIRROR_PATH);
                    Element nameElement = (Element) pathList.item(0);

                    NodeList textPathList = nameElement.getChildNodes();
                    String path = textPathList.item(0).getNodeValue().trim();
                    
                    //------- Get Mirror typemask
                    NodeList typemaskList = firstShowElement.getElementsByTagName(
                        MIRROR_TYPEMASK);
                    Element typemaskElement = (Element) typemaskList.item(0);

                    NodeList typeMaskList = typemaskElement.getChildNodes();
                    Integer typeMask = Integer.parseInt(
                        typeMaskList.item(0).getNodeValue().trim());

                    if (isXmlMirror(typeMask)){
                        xmlMirrors.add(new Mirror(id, path, typeMask));
                    }
                    
                    if (isBannerMirror(typeMask)){
                        bannerMirrors.add(new Mirror(id, path, typeMask));
                    }
                    
                    if (isZipMirror(typeMask)){
                        zipMirrors.add(new Mirror(id, path, typeMask));
                    }
                    
                }//end of if clause
            }//end of for loop with s var
        }
    }

    public Map<Integer, String> search(String name, String showLanguage) {

		String encoded = name;

		try {
			encoded = URLEncoder.encode(name,"UTF-8");
		} catch (UnsupportedEncodingException eee) {
			if (log.isDebugEnabled()) {
				log.debug("Could not encode : " + name);
			}
		}

		
        //Get back XML Document
        Document xmlResults = WebHelper.readURL(String.format(URL_SEARCH_SHOW,
				encoded, showLanguage));
        
        Map<Integer,String> results = new HashMap<Integer, String>();

        if (xmlResults != null) {
            // normalize text representation
            xmlResults.getDocumentElement().normalize();


            NodeList listOfShows = xmlResults.getElementsByTagName(SHOW);
            int totalShows = listOfShows.getLength();
            if (log.isDebugEnabled()) {
                log.debug("Found " + totalShows + " shows");
            }

            //iterate through shows
            for (int s = 0; s < listOfShows.getLength(); s++) {
                Node firstShowNode = listOfShows.item(s);
                if (firstShowNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstShowElement = (Element) firstShowNode;

                    //------- Get Show id
                    NodeList idList = firstShowElement.getElementsByTagName(
                        SHOW_ID);
                    Element idElement = (Element) idList.item(0);

                    NodeList textIdList = idElement.getChildNodes();
                    Integer id = Integer.parseInt(
                        textIdList.item(0).getNodeValue().trim());

                    //------- Get Show name
                    NodeList nameList = firstShowElement.getElementsByTagName(
                        SHOW_NAME);
                    Element nameElement = (Element) nameList.item(0);

                    NodeList textNameList = nameElement.getChildNodes();
                    String showName = textNameList.item(0).getNodeValue().trim();
                    
                    //------- Get Show language
                    NodeList languageList = firstShowElement.getElementsByTagName(
                        SHOW_LANGUAGE);
                    Element languageElement = (Element) languageList.item(0);

                    NodeList textLanguageList = languageElement.getChildNodes();
                    String language = textLanguageList.item(0).getNodeValue().trim();
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Found " + showName + " (" + id + ")");
                    }
                    
                    if (!language.equals(showLanguage)) {
                        if (!results.containsKey(id)) {
                            results.put(id, showName);
                        }
                    } else {
                        results.put(id, showName);
                    }
                    
                }//end of if clause
            }//end of for loop with s var
        }
        
        return results;
    }

    public void createOrUpdate(int id, String language) {

        //Init variables
        ShowsService showsService = serviceContext.newService(ShowsService.class);
        SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);
        EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
        EpisodesManagerConfig config = serviceContext.getEpisodesManagerConfig();
        File destinationDirectory = config.getTempDirectory();
        String zipMirror = getZipMirror().getPath();
        String bannerMirror = getBannerMirror().getPath();
        String apiKey = config.getTheTvDbApiKey();

        try {
            URL downloadUrl = new URL(String.format(URL_DOWNLOAD_SHOW,zipMirror,apiKey,id,language));

            //Get back zip containing all the infos
            WebHelper.downloadFile(destinationDirectory, TEMP_ZIP_FILENAME, downloadUrl);
        } catch (Exception eee) {
            log.error("Could not download show info ", eee);
        }

        //Unzip file
        File source = new File(destinationDirectory, TEMP_ZIP_FILENAME);
        extractZip(source, destinationDirectory);

        //Read language.xml file
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(destinationDirectory, language + ".xml"));

            if (doc != null) {
                doc.getDocumentElement().normalize();

                //Get series info
                NodeList listOfSeries = doc.getElementsByTagName("Series");

                //get only the first node, only one in document.
                Node series = listOfSeries.item(0);

                Element seriesElement = (Element)series;

                //----------- Get Show name
                String showName =
                        XMLParser.getStringValue(seriesElement, SERIES_NAME);

                //----------- Get content rating
                String contentRating =
                        XMLParser.getStringValue(seriesElement, SERIES_CONTENT_RATING);

                //----------- Get first aired
                DateFormat firstAiredFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date firstAired =
                        XMLParser.getDateValue(seriesElement, SERIES_FIRST_AIRED,
                                firstAiredFormat);

                //----------- Get Genre
                List<String> genresList =
                        XMLParser.getStringListValue(seriesElement, SERIES_GENRE, "\\|");

                //----------- Get IMDB id
                String imdbId =
                        XMLParser.getStringValue(seriesElement, SERIES_IMDB_ID);

                //----------- Get Zap2it id
                String zap2itId =
                        XMLParser.getStringValue(seriesElement, SERIES_ZAP2IT_ID);

                //----------- Get network
                String network =
                        XMLParser.getStringValue(seriesElement, SERIES_NETWORK);

                //----------- Get summary
                String summary =
                        XMLParser.getStringValue(seriesElement, SERIES_OVERVIEW);

                //----------- Get status
                NodeList statusList = seriesElement.getElementsByTagName(SERIES_STATUS);
                Element statusElement = (Element) statusList.item(0);
                NodeList textStatusList = statusElement.getChildNodes();
                String statusText = textStatusList.item(0).getNodeValue().trim();
                Boolean status = statusText.equals("Ended");

                //----------- Get runtime
                int runtime =
                        XMLParser.getIntegerValue(seriesElement, SERIES_RUNTIME);

                //----------- Get banner
                String bannerUrlEnd =
                        XMLParser.getStringValue(seriesElement, SERIES_BANNER);
                URL downloadUrl = new URL(String.format(URL_DOWNLOAD_BANNER, bannerMirror, bannerUrlEnd));
                //TODO JC 25/08/2011 download banner

                //----------- Get last updated
                int lastUpdated =
                        XMLParser.getIntegerValue(seriesElement, SERIES_LAST_UPDATED);
                
                //----------- Get actors
                List<String> actorsList =
                        XMLParser.getStringListValue(seriesElement, SERIES_ACTORS, "\\|");


                //Import Series in database
                if (log.isDebugEnabled()) {
                    log.debug("Adding show " + showName);
                }
                Show show = showsService.getShowByTvDbId(id);

				if (show == null) {
					show = showsService.createShow(showName);
				}

				show.setTitle(showName);
				show.setThetvdbId(id);
				show.setNetwork(network);
				show.setRuntime(runtime);
				show.setOver(status);
				show.setActors(actorsList);
				show.setGenres(genresList);
				show.setLastUpdated(lastUpdated);
				show.setSummary(summary);
				show.setZap2itId(zap2itId);
				show.setImdbId(imdbId);
				show.setContentRating(contentRating);
				show.setFirstAired(firstAired);

				showsService.updateShow(show);


				/**************************************************************
				 *  GET INFO FOR EPISODES
				 **************************************************************/

                //Get series info
                NodeList listOfEpisodes = doc.getElementsByTagName("Episode");

                for (int i=0;i<listOfEpisodes.getLength();i++) {

                    //Get the episode node
                    Node episodeNode = listOfEpisodes.item(i);
                    Element episodeElement = (Element) episodeNode;

                    //----------- Get Episode name
                    String episodeName =
                            XMLParser.getStringValue(episodeElement, EPISODE_NAME);

                    //----------- Get Episode season
                    Integer episodeSeason =
                            XMLParser.getIntegerValue(episodeElement, EPISODE_SEASON);

                    //----------- Get Episode number
                    Integer episodeNumber =
                            XMLParser.getIntegerValue(episodeElement, EPISODE_NUMBER);

                    //----------- Get Episode director
                    String episodeDirector =
                            XMLParser.getStringValue(episodeElement, EPISODE_DIRECTOR);

                    //----------- Get Episode first aired
                    Date episodeFirstAired =
                            XMLParser.getDateValue(episodeElement,
                                    EPISODE_FIRST_AIRED, firstAiredFormat);

                    //----------- Get episode guest stars
                    List<String> episodeGuestStars =
                            XMLParser.getStringListValue(seriesElement,
                                    EPISODE_GUEST_STARS,"\\|");

                    //----------- Get episode summary
                    String episodeSummary =
                            XMLParser.getStringValue(episodeElement, EPISODE_SUMMARY);

                    //----------- Get episode imdb id
                    String episodeImdbId =
                            XMLParser.getStringValue(episodeElement, EPISODE_IMDB_ID);

                    //----------- Get episode writer
                    String episodeWriter =
                            XMLParser.getStringValue(episodeElement, EPISODE_WRITER);

                    //----------- Get episode last updated
                    int episodeLastUpdated =
                            XMLParser.getIntegerValue(episodeElement, EPISODE_LAST_UPDATED);

                    //----------- Get episode last updated
                    int episodeTheTvDbId =
                            XMLParser.getIntegerValue(episodeElement, EPISODE_THETVDB_ID);

                    //----------- Get episode screenshot
                    //TODO JC 26/08/2011 get episode screenshot

                    //Get season, if season does not exists, create it
                    Season season;
                    if (!seasonsService.seasonExists(show, episodeSeason)) {
                        try {
                            season = seasonsService.createSeason(show, episodeSeason);
                        } catch (Exception te) {
                            log.error("An error occurred : ", te);
                            return;
                        }
                    } else {
                        season = seasonsService.getSeasonByNumber(show, episodeSeason);
                    }

                    //Create episode, throw exception if already exists
                    Episode episode;

                    if (!episodesService.episodeExistsByNumber(season, episodeNumber)) {
                        try {
                            episode = episodesService.createEpisode(season, episodeNumber, episodeName);
                            episode.setIgnored(false);
                        } catch (Exception eee) {
                            log.error("An error occurred : ", eee);
                            return;
                        }
                    } else {
                        try {
                            episode = episodesService.getEpisodeByNumber(season, episodeNumber);
                        } catch (Exception eee) {
                            log.error("An error occurred : ", eee);
                            return;
                        }
                    }

                    if (null != episode) {
                        episode.setAiringDate(episodeFirstAired);
                        episode.setTitle(episodeName);
                        episode.setDirector(episodeDirector);
                        episode.setSummary(episodeSummary);
                        episode.setImdbId(episodeImdbId);
                        episode.setWriter(episodeWriter);
                        episode.setGuestStars(episodeGuestStars);
                        episode.setThetvdbId(episodeTheTvDbId);
                        episode.setLastUpdated(episodeLastUpdated);
                        episodesService.updateEpisode(episode);
                    }
                }
            }
        } catch (Exception eee) {
            log.error("Could not extract info from XML file", eee);
        }


    }

    protected void extractZip(File source, File destinationDirectory) {
        ZipInputStream zipIS = null;
        FileOutputStream out = null;
        try {
            byte[] buf = new byte[1024];
            ZipEntry zipEntry;
            zipIS = new ZipInputStream(new FileInputStream(source));

            zipEntry = zipIS.getNextEntry();
            while (zipEntry != null) {
                //for each entry to be extracted
                String entryName = zipEntry.getName();
                if (log.isDebugEnabled()) {
                    log.debug("Extracting : " + entryName);
                }
                int n;
                File newFile = new File(destinationDirectory, entryName);

                out = new FileOutputStream(newFile);

                while ((n = zipIS.read(buf, 0, 1024)) > -1)
                    out.write(buf, 0, n);

                out.close();
                zipIS.closeEntry();
                zipEntry = zipIS.getNextEntry();

            }//while


        } catch (Exception eee) {
            log.error("Could not extract zip file", eee);
        } finally {
            try {
                if (zipIS != null) {
                    zipIS.close();
                }
            } catch (IOException eee) {
                log.debug("Could not close zip file", eee);
            }

            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException eee) {
                log.debug("Could not close extracted file", eee);
            }
        }
    }

    public void updateShows() {

        ShowsService service = serviceContext.newService(ShowsService.class);
        EpisodesManagerConfig config = serviceContext.getEpisodesManagerConfig();

        //Get back last updated date
        long lastUpdated = config.getTheTvDbLastUpdated();

        //Get back list of shows to update
        try {
            URL downloadUrl = new URL(String.format(URL_DOWNLOAD_UPDATES, lastUpdated));

            //Get back zip containing all the infos
            Document xmlResults = WebHelper.readURL(downloadUrl.toString());

            if (xmlResults != null) {
                // normalize text representation
                xmlResults.getDocumentElement().normalize();

				Long updateTimestamp = 1L;
				NodeList listOfUpdateTime = xmlResults.getElementsByTagName(UPDATE_TIME);
				if (null != listOfUpdateTime.item(0)){
            		NodeList textNodeList = listOfUpdateTime.item(0).getChildNodes();
            		if (null != textNodeList.item(0) && null != textNodeList.item(0).getNodeValue()) {
                		updateTimestamp = Long.valueOf(textNodeList.item(0).getNodeValue().trim());
            		}
        		}
				config.setTheTvDbLastUpdated(updateTimestamp);
				config.save();

                NodeList listOfUpdatedShows = xmlResults.getElementsByTagName(UPDATE_SHOWS);
                int totalShows = listOfUpdatedShows.getLength();
                if (log.isDebugEnabled()) {
                    log.debug("Found " + totalShows + " shows to update since " + lastUpdated);
                }

                //iterate through shows
                for (int s = 0; s < listOfUpdatedShows.getLength(); s++) {
                    Element idElement = (Element) listOfUpdatedShows.item(s);
                    NodeList textIdList = idElement.getChildNodes();
                    Integer id = Integer.parseInt(textIdList.item(0).getNodeValue().trim());

                    if (service.showExistsFromTvDbId(id)) {
                        //If show is in database, update it
                        createOrUpdate(id, "fr");
                    }
                }//end of for loop with s var
            }

        } catch (Exception eee) {
            log.error("Could not download show info ", eee);
        }
    }
    
    /**
     * Class Mirror to save internally the mirrors info
     */
    protected class Mirror {
        
        protected int id;
        protected String path;
        protected int typemask;
        
        public Mirror (int id, String path, int typemask) {
            this.id = id;
            this.path = path;
            this.typemask = typemask;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getTypemask() {
            return typemask;
        }

        public void setTypemask(int typemask) {
            this.typemask = typemask;
        }
    }
    
    protected boolean isXmlMirror(int typemask) {
        int operationResult = typemask - MIRROR_TYPEMASK_XML;
        
        boolean result = operationResult > 0;
        
        if (log.isDebugEnabled()) {
            log.debug("Mirror with typemask : " + typemask +
                " is Xml mirror ? : " + result);
        }
        
        return result;
    }
    
    protected boolean isBannerMirror(int typemask) {
        int operationResult = typemask - MIRROR_TYPEMASK_BANNER;
        
        boolean result = operationResult > 0;
        
        if (log.isDebugEnabled()) {
            log.debug("Mirror with typemask : " + typemask +
                " is Banner mirror ? : " + result);
        }
        
        return result;
    }
    
    protected boolean isZipMirror(int typemask) {
        
        int operationResult = typemask - MIRROR_TYPEMASK_ZIP;
        
        boolean result = operationResult > 0;
        
        if (log.isDebugEnabled()) {
            log.debug("Mirror with typemask : " + typemask +
                " is Zip mirror ? : " + result);
        }
        
        return result;
    }
    
    protected Mirror getXmlMirror() {
        
        int index = getRandomMirror(0, xmlMirrors.size()-1);
        
        return xmlMirrors.get(index);
    }

    protected Mirror getZipMirror() {

        int index = getRandomMirror(0, zipMirrors.size()-1);

        return zipMirrors.get(index);
    }

    protected Mirror getBannerMirror() {

        int index = getRandomMirror(0, bannerMirrors.size()-1);

        return bannerMirrors.get(index);
    }
    
    protected int getRandomMirror(int minIndex, int maxIndex) {
        
        if (minIndex == maxIndex) {
            return minIndex;
        } else {
            return minIndex + (int)(Math.random() * ((maxIndex - minIndex) + 1));
        }
        
    }

    /**
     * 
     * Download banner for a show. Store it as artworkDirectory/showId/banner.jpg
     * 
     * @param source the banner URL
     * @param show the show for which to get the banner
     */
    protected void downloadBanner(URL source, Show show) {
        int showId = show.getThetvdbId();

        EpisodesManagerConfig config = serviceContext.getEpisodesManagerConfig();
        
        File artworkDirectory = config.getArtworkDirectory();
        
        File showArtworkFile = new File(artworkDirectory, Integer.toString(showId));
        showArtworkFile.mkdir();
        
        WebHelper.downloadFile(showArtworkFile, "banner.jpg", source);
        
    }
}