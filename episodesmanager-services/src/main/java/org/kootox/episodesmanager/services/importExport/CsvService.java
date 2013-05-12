package org.kootox.episodesmanager.services.importExport;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.EpisodeDAO;
import org.kootox.episodesmanager.entities.EpisodesManagerDAOHelper;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class CsvService implements EpisodesManagerService {

    protected ServiceContext serviceContext;

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(CsvService.class);

    /**
     * Method to import lists of episodes from a CSV file.
     * @param file The file to import from.
     */
    public void importFromCSV(File file) {

        ShowsService showsService = serviceContext.newService(ShowsService.class);
        EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
        SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);



        try {
            CSVReader reader = new CSVReader(new FileReader(file), ';');
            List<String[]> myEntries = reader.readAll();

            //remove the headers
            myEntries.remove(0);

            for (String[] episode : myEntries) {
                Show show = null;
                Season season = null;
                Episode newEpisode = null;
                log.debug("Importing episode : " + episode[0] + " - " + episode[2]);
                if (showsService.showExists(episode[0])) {
                    log.debug("The show exists, getting it from the database");
                    show = showsService.getShowByName(episode[0]);
                } else {
                    log.debug("The show does not exist, creating it");
                    try {
                        show = showsService.createShow(episode[0]);
                    } catch (AlreadyExistException aee) {
                        log.error("Show already exists, not normal", aee);
                    }
                }

                log.debug("Using show : " + show.getTitle());


                if (seasonsService.seasonExists(show, Integer.parseInt(episode[1]))) {
                    log.debug("Season exist, getting it from the database");
                    season = seasonsService.getSeasonByNumber(show, Integer.parseInt(episode[1]));
                } else {
                    log.debug("The season does not exist, creating it");
                    try {
                        season = seasonsService.createSeason(show, Integer.parseInt(episode[1]));
                    } catch (AlreadyExistException aee) {
                        log.error("The season " + episode[1] +
                                " already exists, it shouldn't");
                    }
                }

                log.debug("Using season " + season.getNumber() + ";");

                if (episodesService.episodeExistsByNumber(season, Integer.parseInt(episode[6]))) {
                    log.debug("Episode already exists, updating it");
                    Collection<Episode> episodes = episodesService.getAllEpisodes(season);
                    for (Episode tempEpisode : episodes) {
                        if (tempEpisode.getTitle().equals(episode[2])) {
                            newEpisode = tempEpisode;
                        }
                    }
                } else {
                    log.debug("Episode does not exist, creating it");
                    DateFormat formatter = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    try {
                        newEpisode = episodesService.createEpisode(season, Integer.parseInt(episode[6]), episode[2]);
                    } catch (AlreadyExistException aee) {
                        log.error(episode[3] +
                                "already exists but it should not");
                    }
                    newEpisode.setAcquired(Boolean.parseBoolean(episode[4]));
                    if (!"null".equals(episode[3])) {
                        newEpisode.setAiringDate(formatter.parse(episode[3]));
                    }
                    newEpisode.setViewed(Boolean.parseBoolean(episode[5]));
                }
                showsService.updateShow(show);
                seasonsService.updateSeason(season);
                episodesService.updateEpisode(newEpisode);
            }
        } catch (IOException ioe) {
            log.error(
                    "An error occured while trying to import database from CSV",
                    ioe);
        } catch (ParseException pe) {
            log.error(
                    "An error occured while trying to import database from CSV",
                    pe);
        }
    }

    /**
     * Method to export database to CSV
     * @param file The file in which to export.
     */
    public void exportToCSV(File file) {

        TopiaContext newContext = null;

        try {

            //Get all the episodes
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            Collection<Episode> episodes = episodeDAO.findAll();

            log.info("Exporting : " + episodes.size() + " episodes");

            BufferedWriter writer = null;

            try {

                //Create a writer for the directory/filename.csv file
                Writer out = new FileWriter(file);

                writer = new BufferedWriter(out);

                //Write the column headers
                writer.write("Show;Season;Season Number;Episode;" +
                        "Episode Airing Date;Acquired;Viewed;Episode Number\n");

                //Run through all episodes
                for (Episode episode : episodes) {

                    log.info("Exporting episode : " + episode.getTitle());

                    //Write a line for each episode, with also the show and season infos

                    //Write the show info
                    writer.write(episode.getSeason().getShow().getTitle() + ";");
                    //Write the season info
                    writer.write(episode.getSeason().getNumber() + ";");
                    //Write the episode info
                    writer.write(episode.getTitle() + ";" +
                            episode.getAiringDate() + ";" +
                            episode.getAcquired() + ";" +
                            episode.getViewed() + ";" +
                            episode.getNumber());
                    //Go to next line
                    writer.write("\n");
                }

                writer.close();

            } catch (IOException ioe) {
                newContext.closeContext();
                log.error("An error occured while trying to export database to CSV",
                        ioe);
            } finally {
                try {
                    writer.close();
                } catch (IOException eee) {
                    log.error("Error trying to close file");
                } catch (NullPointerException eee) {
                    log.debug("Stream was not existing");
                }
            }

        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }

    }
}
