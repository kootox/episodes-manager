package org.kootox.episodesmanager.services.shows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.EpisodeDAO;
import org.kootox.episodesmanager.entities.EpisodesManagerDAOHelper;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.SeasonDAO;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.entities.ShowDAO;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;
import org.nuiton.topia.framework.TopiaQuery;
import org.nuiton.topia.persistence.TopiaEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class SeasonsService implements EpisodesManagerService {

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(SeasonsService.class);

    protected ServiceContext serviceContext;

    @Override
    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Method to check if a season exists in a show from its number.
     * @param show The show from which you want to check
     * @param number The number of the show you want to check
     * @return true if it exists, false otherwise or if you cannot connect to
     * the database.
     */
    public Boolean seasonExists(Show show, int number) {
        Boolean bool = false;
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            if (show != null) {
                Show tempShow = showDAO.findByTitle(show.getTitle());
                SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);
                List<Season> seasons = seasonDAO.findAllByShow(tempShow);
                for (Season season : seasons) {
                    if (number == season.getNumber()) {
                        bool = true;
                    }
                }
            }
        } catch (TopiaException te) {
            log.info("An error occurred : ", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return bool;
    }

    /**
     * Create a season with the title in the show given in parameter.
     *
     * @param show The show to get the new season
     * @param number The season number
     * @return The newly created season, null if an error occurred
     * @throws AlreadyExistException if a season already exists with this name
     */
    public Season createSeason(Show show, Integer number) throws AlreadyExistException {

        Season season = null;
        TopiaContext newContext = null;

        if (show != null) {
            try {
                //init the context
                newContext = serviceContext.getTransaction();
                //get the season DAO
                SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);

                //if season with same name does not already exists
                if (!seasonExists(show, number)) {

                    //Create a map of properties and put into it the properties we
                    //already know : title and show
                    Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put("show", show);
                    properties.put("number", number);

                    //Create the season with the properties
                    season = seasonDAO.create(properties);

                    //Commit transaction and close context
                    newContext.commitTransaction();

                    //return the created season
                    return season;
                } else {
                    throw new AlreadyExistException("Season " + number +
                            "already exists for show" + show.getTitle());
                }
            } catch (TopiaException te) {
                log.error("An error occurred", te);
            } finally {
                serviceContext.closeTransaction(newContext);
            }
        }
        return season;
    }

    /**
     * Method to get a season of a show by its number.
     * @param show The show from which you want a season
     * @param number The number of the season you want to get (season 1, 2, ...)
     * @return The season, or null if it is not found or if you cannot connect
     * to the database.
     */
    public Season getSeasonByNumber(Show show, int number) {


        //Get all the seasons of the show
        Collection<Season> seasons = getAllSeasons(show);
        //for all seasons
        for (Season season : seasons) {
            //if number is the correct one
            if (number == season.getNumber()) {
                //return the season
                return season;
            }
        }

        //Return null if not found
        return null;
    }

    /**
     * Method to get a season from its id.
     * @param id The season id
     * @return The season, or null if it is not found or if you cannot connect
     * to the database.
     */
    public Season getSeasonById(String id) {

        Season season = null;
        TopiaContext newContext = null;

        try {
                //init the context
                newContext = serviceContext.getTransaction();
                //get the season DAO
                SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);

                season = seasonDAO.findByTopiaId(id);

            } catch (TopiaException te) {
                log.error("An error occurred", te);
            } finally {
                serviceContext.closeTransaction(newContext);
            }
        return season;
    }

    /**
     * Method to get all the seasons of a show
     * @param show The show from which you want to get the seasons
     * @return A list containing all the seasons. It is empty if you cannot
     * connect to the database.
     */
    public List<Season> getAllSeasons(Show show) {
        List<Season> seasons = new ArrayList<Season>();
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);
            TopiaQuery query = seasonDAO.createQuery();
            query.addEquals(Season.PROPERTY_SHOW, show);
            query.addOrder(Season.PROPERTY_NUMBER);
            seasons = query.executeToEntityList(newContext, Season.class);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return seasons;
    }

    /**
     * Update a season
     * @param season the season to update
     */
    public void updateSeason(Season season) {
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);
            String id = season.getTopiaId();
            Season reloaded = seasonDAO.findByTopiaId(id);
            reloaded.setNumber(season.getNumber());
            seasonDAO.update(reloaded);
            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
    }

    /**
     * Method to know if the season has been completely watched or not
     * @param season the season you want to know about
     * @return true if the season have been entirely watched, false otherwise.
     */
    public Boolean getSeasonWatched(Season season) {
        Boolean watched = true;
        if (season != null) {

            EpisodesService episodesService= serviceContext.newService(EpisodesService.class);

            List<Episode> episodes = episodesService.getAllEpisodes(season);
            for (Episode episode : episodes) {
                if (episode.getViewed() != null) {
                    watched &= episode.getViewed();
                }
            }
        }
        return watched;
    }

    /**
     * Method to know if the season has been completely ignored or not
     * @param season the season you want to know about
     * @return true if the season have been entirely ignored, false otherwise.
     */
    public Boolean getSeasonIgnored(Season season) {
        Boolean ignored = true;
        if (season != null) {

            EpisodesService episodesService= serviceContext.newService(EpisodesService.class);

            List<Episode> episodes = episodesService.getAllEpisodes(season);
            for (Episode episode : episodes) {
                if (episode.getIgnored() != null) {
                    ignored &= episode.getIgnored();
                }
            }
        }
        return ignored;
    }

    /**
     * Method to set the season status and its episodes recursively
     * @param season the season you want to update the status
     * @param acquired the acquired status
     * @param watched the watched status
     */
    public void setSeasonStatus(Season season, Boolean acquired, Boolean watched) {
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            List<Episode> episodes = episodeDAO.findAllBySeason(season);
            for (Episode episode : episodes) {
                if (acquired != null) {
                    episode.setAcquired(acquired);
                }
                if (watched != null) {
                    episode.setViewed(watched);
                }
                episodeDAO.update(episode);
            }
            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
    }

        /**
     * Method to set the season ignored status and its episodes recursively
     * @param season the season you want to update the status
     * @param ignored the ignored status
     */
    public void setSeasonIgnored(Season season, Boolean ignored) {
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            List<Episode> episodes = episodeDAO.findAllBySeason(season);
            for (Episode episode : episodes) {
                if (ignored != null) {
                    episode.setIgnored(ignored);
                }
                episodeDAO.update(episode);
            }
            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
    }

    /**
     * Method to know if the season have been completely acquired or not
     * @param season the season you want to know about
     * @return true if the season have been entirely acquired, false otherwise.
     */
    public Boolean getSeasonAcquired(Season season) {
        Boolean acquired = true;
        if (season != null) {
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            List<Episode> episodes = episodesService.getAllEpisodes(season);
            for (Episode episode : episodes) {
                if (episode.getAcquired() != null) {
                    acquired &= episode.getAcquired();
                }
            }
        }
        return acquired;
    }

    /**
     * Method to get all the seasons of the database
     * @return a list containing all the episodes, empty if an error occurred.
     */
    public List<Season> getAllSeasons() {

        List<Season> seasons = new ArrayList<Season>();
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);
            TopiaQuery query = seasonDAO.createQuery();
            query.addOrder(Season.PROPERTY_NUMBER);
            seasons = query.executeToEntityList(newContext, Season.class);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return seasons;
    }

    /**
     * Method to get the number of seasons of a show
     * @param show the show you want to know the number of seasons
     * @return the number of seasons of the show
     */
    public int getSeasonsCount(Show show) {
        int count = 0;
        if (show != null) {
            TopiaContext newContext = null;
            try {
                newContext = serviceContext.getTransaction();
                SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(newContext);
                count = seasonDAO.findAllByShow(show).size();
            } catch (TopiaException te) {
                log.error("An error occurred while trying to contact database", te);
            } finally {
                serviceContext.closeTransaction(newContext);
            }
        }
        return count;
    }

    public void deleteSeason(String topiaId) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            TopiaEntity entity = transaction.findByTopiaId(topiaId);

            if (entity instanceof Season) {
                SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(transaction);
                seasonDAO.delete((Season) entity);
                transaction.commitTransaction();
            }
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void deleteSeason(Season season) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(transaction);
            Season toDelete = seasonDAO.findByTopiaId(season.getTopiaId());
            seasonDAO.delete(toDelete);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void acquireSeason(String id, Boolean v) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(transaction);
            Season toAcquire = seasonDAO.findByTopiaId(id);
            for (Episode episode:toAcquire.getEpisode()){
                episode.setAcquired(v);
                if (!v) {
                    episode.setViewed(v);
                }
            }
            seasonDAO.update(toAcquire);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void acquireSeason(Season season, Boolean v) {
        acquireSeason(season.getTopiaId(), v);
    }

    public void watchSeason(String id, Boolean v) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            SeasonDAO seasonDAO = EpisodesManagerDAOHelper.getSeasonDAO(transaction);
            Season toWatch = seasonDAO.findByTopiaId(id);
            for (Episode episode:toWatch.getEpisode()){
                episode.setViewed(v);
                if (v) {
                    episode.setAcquired(v);
                }
            }
            seasonDAO.update(toWatch);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void watchSeason(Season season, Boolean v) {
        watchSeason(season.getTopiaId(), v);
    }
}
