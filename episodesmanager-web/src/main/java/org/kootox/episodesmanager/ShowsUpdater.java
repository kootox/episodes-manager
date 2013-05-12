package org.kootox.episodesmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.databases.TheTvDbService;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 * User: couteau
 * Date: 16/09/12
 * Time: 13:43
 */
public class ShowsUpdater {

    static private Log log = LogFactory.getLog(ShowsUpdater.class);

    protected static Timer timer = null;
    protected static TimerTask updateTask = null;

    /**
     * Regularly retrieves the information from all the xml streams
     * and create new forms
     *
     * @param delay  the delay before the first retrieving
     * @param period interval between two retrievings
     */
    public static void updateShows(long delay, long period) {
        if (timer == null) {
            timer = new java.util.Timer();
        }

        if (updateTask == null) {

            updateTask = new TimerTask() {
                @Override
                public void run() {
                    if (log.isInfoEnabled()) {
                        log.info("Update shows task");
                    }

                    TheTvDbService webService = EpisodesManagerHelper.newService(TheTvDbService.class);

                    webService.updateShows();
                }
            };

            timer.scheduleAtFixedRate(updateTask, delay, period);
        }
    }

    /**
     * Cancel regular shows info retrieving
     */
    public static void clearShowsUpdate() {
        if (updateTask != null && timer != null) {
            updateTask.cancel();
            timer.purge();
        }
    }
}
