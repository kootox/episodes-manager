package org.kootox.episodesmanager.services.shows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.EpisodesManagerDAOHelper;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.entities.ShowDAO;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;
import org.nuiton.topia.framework.TopiaQuery;
import org.nuiton.topia.persistence.TopiaEntity;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class ShowsService implements EpisodesManagerService {

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(ShowsService.class);

    protected ServiceContext serviceContext;

    @Override
    public void setServiceContext(ServiceContext serviceContext){
        this.serviceContext = serviceContext;
    }

    /**
     * Method to create a serie from its name. The method test if a serie with a
     * similar name does not already exist in the database.
     *
     * @param title the title of the new serie
     * @return a new Serie that is stored in the database, null if an error
     * occurred
     * @throws AlreadyExistException if the serie already exists
     */
    public Show createShow(String title) throws AlreadyExistException {

        Show show = null; //The show that will be returned
        TopiaContext newContext = null; //The context

        try {
            //init the context
            newContext = serviceContext.getTransaction();
            //get the show DAO
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);

            if (!showExists(title)) {
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("title", title);

                show = showDAO.create(properties);
                newContext.commitTransaction();
            } else {
                throw new AlreadyExistException("Show " + title + " already exists");
            }
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return show;
    }

    /**
     * Method to get a serie with its name (which is unique).
     *
     * @param name Title of the serie that needs to be retrieved from the
     * database.
     * @return the serie from the database, null if an error occurred.
     */
    public Show getShowByName(String name) {
        Show show = null;
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            show = showDAO.findByTitle(name);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return show;
    }

    /**
     * Return all the series from the database
     *
     * @return a List containing all the series from the database. Children
     * objects are not loaded from the database. You will need to call the
     * getSerieByName method on every Serie that needs to have its children
     * objects loaded from the database. Return null if an error occurred while
     * talking to the database.
     */
    public List<Show> getAllShows() {

        List<Show> shows = new ArrayList<Show>();
        TopiaContext newContext = null;
        try {

            newContext = serviceContext.getTransaction();
            //get the episode DAO
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            TopiaQuery query = showDAO.createQuery().
                    addOrder(Show.PROPERTY_TITLE);
            shows = query.executeToEntityList(newContext, Show.class);
        } catch (TopiaException te) {
            log.error("An error occured while trying to contact database", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return shows;
    }

	public Show getShowByTvDbId(int id) {

        Show show = null;
        TopiaContext newContext = null;
        try {

            newContext = serviceContext.getTransaction();
            //get the episode DAO
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
			show = showDAO.findByThetvdbId(id);
        } catch (TopiaException te) {
            log.error("An error occured while trying to contact database", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return show;
    }

    /**
     * Test if a serie exists in the database.
     * @param name the name of the serie that needs to be checked.
     * @return true if the serie exists in the database, false otherwise (or if
     * an error occurred while talking to the database).
     */
    public Boolean showExists(String name) {

        Boolean bool = false;
        TopiaContext newContext = null;

        try {
            newContext = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            Show show = showDAO.findByTitle(name);
            bool = (show != null);

        } catch (TopiaException te) {
            if (log.isInfoEnabled()) {
                log.info("Got a TopiaException, return false", te);
            }
        } finally {
            serviceContext.closeTransaction(newContext);
        }

        return bool;
    }

    /**
     * Update a show
     * @param show the show to update
     */
    public void updateShow(Show show) {
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            String id = show.getTopiaId();
            Show reloaded = showDAO.findByTopiaId(id);
            reloaded.setOver(show.getOver());
            reloaded.setTitle(show.getTitle());
            reloaded.setTvrageId(show.getTvrageId());
            reloaded.setNetwork(show.getNetwork());
            reloaded.setAirtime(show.getAirtime());
            reloaded.setOriginCountry(show.getOriginCountry());
            reloaded.setRuntime(show.getRuntime());
            reloaded.setTimeZone(show.getTimeZone());
            reloaded.setFirstAired(show.getFirstAired());
            reloaded.setActors(show.getActors());
            reloaded.setContentRating(show.getContentRating());
            reloaded.setGenres(show.getGenres());
            reloaded.setImdbId(show.getImdbId());
            reloaded.setThetvdbId(show.getThetvdbId());
            reloaded.setSummary(show.getSummary());
            reloaded.setZap2itId(show.getZap2itId());
            showDAO.update(reloaded);
            newContext.commitTransaction();
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
    }

    public boolean showExistsFromTvDbId(int id) {

        Boolean bool = false;
        TopiaContext newContext = null;
        try {
            newContext = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(newContext);
            Show tempShow = showDAO.findByThetvdbId(id);
            if (tempShow != null) {
                bool = true;
            }
        } catch (TopiaException te) {
            log.info("An error occurred : ", te);
        } finally {
            serviceContext.closeTransaction(newContext);
        }
        return bool;
    }

    /**
     * Method to get a show from its topiaId
     * @param id the topiaId of the show you want to find
     * @return the Show if found, null if an error occurred or the show is not
     * found.
     */
    public Show getShowById(String id) {
        Show show = null;
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(transaction);
            show = showDAO.findByTopiaId(id);
        } catch (TopiaException te) {
            log.error("An error occurred", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
        return show;
    }

    public void deleteShow(String topiaId) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            TopiaEntity entity = transaction.findByTopiaId(topiaId);

            if (entity instanceof Show) {
                ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(transaction);
                showDAO.delete((Show) entity);
                transaction.commitTransaction();
            }
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void deleteShow(Show show) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(transaction);
            Show toDelete = showDAO.findByTopiaId(show.getTopiaId());
            showDAO.delete(toDelete);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }
    }

    public void ignoreShow(Show show) {
        TopiaContext transaction = null;
        try {
            transaction = serviceContext.getTransaction();
            ShowDAO showDAO = EpisodesManagerDAOHelper.getShowDAO(transaction);
            Show toIgnore = showDAO.findByTopiaId(show.getTopiaId());
            toIgnore.setIgnored(true);
            showDAO.update(toIgnore);
            transaction.commitTransaction();
        } catch (TopiaException te) {
            log.error("Topia exception", te);
        } finally {
            serviceContext.closeTransaction(transaction);
        }

    }
}
