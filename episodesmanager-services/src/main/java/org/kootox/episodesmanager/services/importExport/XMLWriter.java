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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 *
 * Class that can export different elements of database into XML
 *
 * User: couteau
 * Date: 12 mars 2010
 */
public class XMLWriter implements EpisodesManagerService {

    private static Log log = LogFactory.getLog(XMLWriter.class);

    protected ServiceContext serviceContext;

    public XMLWriter(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Export an episode into XML
     *
     * @param episode the episode to export into XML
     * @return the episode XML code
     */
    private String exportEpisode(Episode episode) {

        StringBuilder builder = new StringBuilder();

        builder.append("            <episode>\n");

        //title
        builder.append("                <title>");
        builder.append(escape(episode.getTitle()));
        builder.append("</title>\n");

        //airing date
        builder.append("                <airdate>");
        builder.append(episode.getAiringDate());
        builder.append("</airdate>\n");

        //acquired
        builder.append("                <acquired>");
        builder.append(episode.getAcquired());
        builder.append("</acquired>\n");

        //viewed
        builder.append("                <watched>");
        builder.append(episode.getViewed());
        builder.append("</watched>\n");

        //number
        builder.append("                <number>");
        builder.append(episode.getNumber());
        builder.append("</number>\n");

        //summary
        builder.append("                <summary>");
        builder.append(escape(episode.getSummary()));
        builder.append("</summary>\n");

        builder.append("            </episode>\n");

        return builder.toString() ;
    }

    private String exportSeason(Season season){

        EpisodesService episodesService = serviceContext.newService(EpisodesService.class);

        StringBuilder builder = new StringBuilder();

        builder.append("        <season>\n");

        //number
        builder.append("            <number>");
        builder.append(season.getNumber());
        builder.append("</number>\n");

        builder.append("            <episodes>\n");

        List<Episode> episodes = episodesService.getAllEpisodes(season);

        for(Episode episode:episodes){
            builder.append(exportEpisode(episode));
        }

        builder.append("            </episodes>\n");

        builder.append("        </season>\n");

        return builder.toString();
    }

    private String exportShow(Show show){

        SeasonsService service = serviceContext.newService(SeasonsService.class);

        StringBuilder builder = new StringBuilder();

        builder.append("<show>\n");

        //title
        builder.append("    <name>");
        builder.append(escape(show.getTitle()));
        builder.append("</name>\n");

        //tvrage id
        builder.append("    <tvrageID>");
        builder.append(show.getTvrageId());
        builder.append("</tvrageID>\n");

        //over
        builder.append("    <status>");
        builder.append(show.getOver());
        builder.append("</status>\n");

        //origin country
        builder.append("    <origin_country>");
        builder.append(escape(show.getOriginCountry()));
        builder.append("</origin_country>\n");

        //runtime
        builder.append("    <runtime>");
        builder.append(show.getRuntime());
        builder.append("</runtime>\n");

        //network
        builder.append("    <network>");
        builder.append(escape(show.getNetwork()));
        builder.append("</network>\n");

        //airtime
        builder.append("    <airtime>");
        builder.append(show.getAirtime());
        builder.append("</airtime>\n");

        //timezone
        builder.append("    <timezone>");
        builder.append(escape(show.getTimeZone()));
        builder.append("</timezone>\n");

        builder.append("    <seasons>\n");

        List<Season> seasons = service.getAllSeasons(show);

        for(Season season:seasons){
            builder.append(exportSeason(season));
        }

        builder.append("    </seasons>\n");

        builder.append("</show>\n");

        return builder.toString();
    }

    public void exportToXML(File file) {

        ShowsService service = serviceContext.newService(ShowsService.class);

        OutputStreamWriter xmlFile = null;

        try {
            xmlFile = new FileWriter(file);

            List<Show> shows = service.getAllShows();

            StringBuilder builder = new StringBuilder();

            builder.append("<?xml version=\"1.0\"?>\n<shows>\n");

            for(Show show:shows) {
                builder.append(exportShow(show));
            }

            builder.append("</shows>\n");

            xmlFile.write(builder.toString());
            xmlFile.close();
        } catch (Exception ex) {
            log.error("an error occurred", ex);
        } finally {
            try {
                xmlFile.close();
            } catch (IOException eee) {
                log.error("Error trying to close file");
            } catch (NullPointerException eee) {
                log.debug("Stream was not existing");
            }
        }
    }

    private String escape(String toEscape){
        return StringEscapeUtils.escapeXml(toEscape);
    }
}
