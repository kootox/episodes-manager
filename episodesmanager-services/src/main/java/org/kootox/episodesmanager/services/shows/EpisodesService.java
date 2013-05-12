package org.kootox.episodesmanager.services.shows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.*;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;
import org.nuiton.topia.framework.TopiaQuery;
import org.nuiton.topia.persistence.TopiaEntity;

import java.util.*;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class EpisodesService implements EpisodesManagerService {

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(EpisodesService.class);

    protected ServiceContext serviceContext;

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Method to check if an episode exists in a season from its number.
     * @param season The season in which you want to check
     * @param number The number of the episode
     * @return true if found, false otherwise or if an error occurred.
     */
    public Boolean episodeExistsByNumber(Season season, int number) {
        Boolean bool = false;
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            TopiaQuery query = episodeDAO.createQuery();
            query.addEquals(Episode.PROPERTY_SEASON, season);
            query.addEquals(Episode.PROPERTY_NUMBER, number);
            Episode episode = query.executeToEntity(newContext, Episode.class);
            if (episode != null) {
                bool = true;
            }
        } catch (TopiaException te) {
            log.info("topia exception", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return bool;
    }

    /**
     * Method to create an episode in a season
     * @param season The season in which you want to create the episode.
     * @param title The title of the episode.
     * @param number The number of the episode.
     * @return The created episode, null if an error occurred.
     * @throws AlreadyExistException if an episode with the same name already
     * exists in this season.
     */
    public Episode createEpisode(Season season, Integer number, String title)
            throws AlreadyExistException {

        Episode episode = null;
        TopiaContext newContext = null;

        try {

            //init the context
            newContext = serviceContext.getTransaction();
            //get the season DAO
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            //if season with same name does not already exists
            if (!episodeExistsByNumber(season, number)) {

                //Create a map of properties and put into it the properties we
                //already know : title and show
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("title", title);
                properties.put("season", season);
                properties.put("number", number);
                properties.put("acquired", false);
                properties.put("viewed", false);

                //Create the season with the properties
                episode = episodeDAO.create(properties);

                //Commit transaction and close context
                newContext.commitTransaction();
            } else {
                throw new AlreadyExistException("Episode " + title +
                        " already exists for season " + season.getNumber());
            }
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }

        return episode;
    }

    /**
     * Method to get an episode from a season by its number.
     * @param season The season in which you want to search.
     * @param number The number of the episode you want to find.
     * @return The episode if found, null if it does not exists or if an error
     * occurred.
     */
    public Episode getEpisodeByNumber(Season season, int number) {
        Episode episode = null;
        TopiaContext newContext = null;
        try {
            //init the context
            newContext = serviceContext.getTransaction();
            //get the episode DAO
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            TopiaQuery query = episodeDAO.createQuery();
            query.addEquals(Episode.PROPERTY_SEASON, season);
            query.addEquals(Episode.PROPERTY_NUMBER, number);
            episode = query.executeToEntity(newContext, Episode.class);

            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return episode;
    }
    
    /**
     * Method to get an episode from its id.
     * @return The episode if found, null if it does not exists or if an error
     * occurred.
     */
    public Episode getEpisodeById(String id) {
        Episode episode = null;
        TopiaContext newContext = null;
        try {
            //init the context
            newContext = serviceContext.getTransaction();
            //get the episode DAO
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            episode = episodeDAO.findByTopiaId(id);
            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return episode;
    }

    /**
     * Method to get all the episodes of a season
     * @param season The season from which you want the episodes
     * @return A list containing all the episodes found. Empty if an error
     * occurred.
     */
    public List<Episode> getAllEpisodes(Season season) {
        List<Episode> episodes = new ArrayList<Episode>();
        TopiaContext newContext = null;
        try {
            //init the context
            newContext = serviceContext.getTransaction();
            //get the episode DAO
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            episodes = episodeDAO.createQuery().
                    addEquals(Episode.PROPERTY_SEASON, season).
                    addLoad(Episode.PROPERTY_SEASON).
                    addOrder(Episode.PROPERTY_AIRING_DATE).
                    executeToEntityList(newContext, Episode.class);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return episodes;
    }

    /**
     * Update an episode
     * @param episode the episode to update
     */
    public void updateEpisode(Episode episode) {
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            String id = episode.getTopiaId();
            Episode reloaded = episodeDAO.findByTopiaId(id);
            reloaded.setAcquired(episode.getAcquired());
            reloaded.setViewed(episode.getViewed());
            reloaded.setAiringDate(episode.getAiringDate());
            reloaded.setNumber(episode.getNumber());
            reloaded.setSummary(episode.getSummary());
            reloaded.setTitle(episode.getTitle());
            reloaded.setIgnored(episode.getIgnored());
            episodeDAO.update(reloaded);

            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
    }

    /**
     * Method to get all the episodes in the database
     * @return a list containing all the episodes, empty if an error occurred.
     */
    public List<Episode> getAllEpisodes() {

        List<Episode> episodes = new ArrayList<Episode>();
        TopiaContext newContext = null;

        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            episodes = episodeDAO.findAll();
            episodes.size();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return episodes;
    }

    /**
     * Method to get all the episodes to be acquired (already broadcast and not
     * acquired).
     * @return a list of episodes.
     */
    public List<Episode> getToAcquireEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();
        List<Episode> toAcquire = new ArrayList<Episode>();
        List<String> added = new ArrayList<String>();
        TopiaContext newContext = null;
        try {
            //Get all the acquired episodes
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
            TopiaQuery query = episodeDAO.createQuery("E");
            query.addEquals(Episode.PROPERTY_ACQUIRED, false);
            query.addWhere(Episode.PROPERTY_AIRING_DATE, TopiaQuery.Op.LT, Calendar.getInstance().getTime());
            query.addWhere(Episode.PROPERTY_IGNORED, TopiaQuery.Op.NEQ, true);
            query.addWhere("E.airingDate != '-1-11-30 00:00:00.0'");
            query.addWhere("E.airingDate != '0002-11-30 00:00:00.0'");
            query.addOrder(Episode.PROPERTY_AIRING_DATE);
            query.addOrder(Episode.PROPERTY_NUMBER);
            episodes = episodeDAO.findAllByQuery(query);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        
        for (Episode episode:episodes) {
            Season season = episode.getSeason();
            if (season != null) {
                Show show = season.getShow();
                if (show != null) {
                    if (episode != null && !added.contains(show.getTitle())) {
                        added.add(show.getTitle());
                        toAcquire.add(episode);
                    }
                }
            }
        }
        
        return toAcquire;
    }

    /**
     * Method to get all the episodes to be watched (already acquired and not
     * watched).
     * @return a list of episodes.
     */
    public List<Episode> getToWatchEpisodes() {

        //List<Object[]> res = new ArrayList<Object[]>();
        List<Episode> episodes = new ArrayList<Episode>();
        List<Episode> toWatch = new ArrayList<Episode>();
        List<String> added = new ArrayList<String>();
        TopiaContext newContext = null;

        try {
            //Get all the acquired episodes
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            TopiaQuery query = episodeDAO.createQuery("E");
            query.addEquals(Episode.PROPERTY_ACQUIRED, true);
            query.addEquals(Episode.PROPERTY_VIEWED, false);
            query.addWhere(Episode.PROPERTY_IGNORED, TopiaQuery.Op.NEQ, true);
            query.addWhere("E.airingDate != '-1-11-30 00:00:00.0'");
            query.addOrder(Episode.PROPERTY_AIRING_DATE);
            query.addOrder(Episode.PROPERTY_NUMBER);
            
            episodes = episodeDAO.findAllByQuery(query);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        
        for (Episode episode:episodes) {
            Season season = episode.getSeason();
            if (season != null) {
                Show show = season.getShow();
                if (show != null) {
                    if (episode != null && !added.contains(show.getTitle())) {
                        added.add(show.getTitle());
                        toWatch.add(episode);
                    }
                }
            }
        }
        
        return toWatch;
    }

    public List<Episode> getFutureEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();
        List<Episode> future = new ArrayList<Episode>();
        List<String> added = new ArrayList<String>();
        TopiaContext newContext = null;

        try {
            //Get all the acquired episodes
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            TopiaQuery query = episodeDAO.createQuery("E");
            query.setSelect("E, E.season.number, E.season.show.title");
            query.addEquals(Episode.PROPERTY_ACQUIRED, false);
            query.addEquals(Episode.PROPERTY_VIEWED, false);
            query.addWhere("E.airingDate > current_date()");
            query.addWhere("E.airingDate != '-1-11-30 00:00:00.0'");
            query.addWhere(Episode.PROPERTY_IGNORED, TopiaQuery.Op.NEQ, true);
            query.addOrder(Episode.PROPERTY_AIRING_DATE);
            query.addOrder(Episode.PROPERTY_NUMBER);
            episodes = episodeDAO.findAllByQuery(query);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        
        for (Episode episode:episodes) {
            Season season = episode.getSeason();
            if (season != null) {
                Show show = season.getShow();
                if (show != null) {
                    if (episode != null && !added.contains(show.getTitle())) {
                        added.add(show.getTitle());
                        future.add(episode);
                    }
                }
            }
        }
        
        
        return future;
    }

    /**
     * Method to get the number of episodes of a season
     * @param season the season you want to know the number of episodes
     * @return the number of episodes of the season
     */
    public int getEpisodesCount(Season season) {
        int count = 0;
        if (season != null) {
            TopiaContext newContext = null;
            try {
                newContext = serviceContext.getTransaction();
                EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);
                count = episodeDAO.findAllBySeason(season).size();
            } catch (TopiaException te) {
                log.error("An error occured while trying to contact database", te);
            } finally {
                serviceContext.closeTransaction(newContext);
            }
        }
        return count;
    }

    public List<Object[]> getTimeSpentTable() {
        List<Object[]> res = new ArrayList<Object[]>();
        TopiaContext newContext = null;

        try {
            newContext = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(newContext);

            TopiaQuery query = episodeDAO.createQuery("E");
            query.setSelect("S." + Show.PROPERTY_TITLE,
                    "S." + Show.PROPERTY_OVER,
                    "S." + Show.PROPERTY_RUNTIME,
                    "COUNT(*)",
                    "S." + Show.PROPERTY_RUNTIME + "*COUNT(*)");
            query.addLeftJoin("E.season", "SE", false);
            query.addLeftJoin("SE.show", "S", false);
            query.addEquals("E." + Episode.PROPERTY_VIEWED, true);
            query.addGroup("S." + Show.PROPERTY_TITLE, "S." + Show.PROPERTY_OVER, "S." + Show.PROPERTY_RUNTIME);
            query.addOrder("S." + Show.PROPERTY_TITLE);

            if (log.isDebugEnabled()) {
                log.debug("Time spent calculation query : " + query.fullQuery());
            }

            res = (List<Object[]>) query.execute(newContext);

        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return res;
    }

    public void deleteEpisode(String topiaId) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            TopiaEntity entity = transaction.findByTopiaId(topiaId);

            if (entity instanceof Episode) {
                EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(transaction);
                episodeDAO.delete((Episode) entity);
                transaction.commitTransaction();
            }
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void deleteEpisode(Episode episode) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(transaction);
            Episode toDelete = episodeDAO.findByTopiaId(episode.getTopiaId());
            episodeDAO.delete(toDelete);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void ignoreEpisode(Episode episode) {
        ignoreEpisodeById(episode.getTopiaId());
    }
    
    public void ignoreEpisodeById(String id){
        
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(transaction);
            Episode toIgnore = episodeDAO.findByTopiaId(id);
            toIgnore.setIgnored(true);
            episodeDAO.update(toIgnore);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
        
    }
    
    public void watchEpisode(Episode episode) {
        watchEpisodeById(episode.getTopiaId());
    }
    
    public void watchEpisodeById(String id){
        
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(transaction);
            Episode toWatch = episodeDAO.findByTopiaId(id);
            toWatch.setViewed(Boolean.TRUE);
            toWatch.setAcquired(Boolean.TRUE);
            episodeDAO.update(toWatch);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
        
    }
    
    public void acquireEpisode(Episode episode) {
        acquireEpisodeById(episode.getTopiaId());
    }
    
    public void acquireEpisodeById(String id){
        
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            EpisodeDAO episodeDAO = EpisodesManagerDAOHelper.getEpisodeDAO(transaction);
            Episode toAcquire = episodeDAO.findByTopiaId(id);
            toAcquire.setAcquired(Boolean.TRUE);
            episodeDAO.update(toAcquire);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
        
    }
}
