package org.kootox.episodesmanager;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.shows.EpisodesService;

/**
 *
 * @author couteau
 */
public class EpisodesOperations extends WebMotionController{
    
    public Render acquire(String id) {
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        episodesService.acquireEpisodeById(id);
        return renderLastPage();
        //return renderAction("Index.index");
    }
    
    public Render watch(String id) {
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        episodesService.watchEpisodeById(id);
        return renderLastPage();
        //return renderAction("Index.index");
    }
    
    public Render ignore(String id) {
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        episodesService.ignoreEpisodeById(id);
        return renderLastPage();
        //return renderAction("Index.index");
    }
    
    public Render view(String id) {
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        Episode episode = episodesService.getEpisodeById(id);
        return renderView("episode.jsp",
                "episode", episode);
    }
    
}
