package org.kootox.episodesmanager;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;

import java.util.List;

/**
 *
 * @author couteau
 */
public class ShowOperations extends WebMotionController {
    
    public Render view(String id) {
        ShowsService showsService = EpisodesManagerHelper.newService(ShowsService.class);
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        Show show = showsService.getShowById(id);
        List<Season> seasons = seasonsService.getAllSeasons(show);
        return renderView("show.jsp",
                "show", show,
                "seasons", seasons);
    }
}
