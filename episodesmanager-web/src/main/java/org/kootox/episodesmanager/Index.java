package org.kootox.episodesmanager;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.ShowsService;

import java.util.List;

/**
 *
 * @author couteau
 */
public class Index extends WebMotionController {
    
    public Render index(List<String> displayed, String filtered) {

        ShowsUpdater.updateShows(0,600000000);
        
        ShowsService service = EpisodesManagerHelper.newService(ShowsService.class);
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        
        int showsNb = service.getAllShows().size();
        
        Boolean acquire = Boolean.TRUE;
        Boolean watch = Boolean.TRUE;
        Boolean broadcast = Boolean.TRUE;
        
        if (displayed != null) {
            if(!displayed.contains("toAcquire")){
                acquire = Boolean.FALSE;
            }

            if(!displayed.contains("toWatch")){
                watch = Boolean.FALSE;
            }

            if(!displayed.contains("toBroadcast")){
                broadcast = Boolean.FALSE;
            }
        } else if ("1".equals(filtered)){
            acquire = Boolean.FALSE;
            watch = Boolean.FALSE;
            broadcast = Boolean.FALSE;
        }
      
        
        List<Episode> toAcquire= episodesService.getToAcquireEpisodes();
        
        List<Episode> toWatch= episodesService.getToWatchEpisodes();
        
        List<Episode> toBroadcast= episodesService.getFutureEpisodes();
        
        return renderView("index.jsp",
                "showsNb", showsNb,
                "episodesToAcquire", toAcquire,
                "episodesToWatch", toWatch,
                "episodesToBroadcast", toBroadcast,
                "acquire", acquire,
                "watch", watch,
                "broadcast", broadcast);
    }
    
    
}
