package org.kootox.episodesmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;

import java.net.URLDecoder;
import java.util.List;

/**
 *
 * @author couteau
 */
public class SeasonOperations extends WebMotionController {

    public Render view(String id) {
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        Season season = seasonsService.getSeasonById(id);
        List<Episode> episodes = episodesService.getAllEpisodes(season);
        return renderView("season.jsp",
                "season", season,
                "episodes", episodes);
    }

    public Render view2(String id) {
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        Season season = seasonsService.getSeasonById(id);
        List<Episode> episodes = episodesService.getAllEpisodes(season);
        Gson gson = new GsonBuilder().setExclusionStrategies()
                .setExclusionStrategies(new EpisodeExclusionStrategy())
                .create();
        String episodesJson = gson.toJson(episodes);

        return renderView("season2.jsp",
                "season", season,
                "episodes", episodesJson);
    }

    public Render episodes(String id) throws Exception{
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        Season season = seasonsService.getSeasonById(URLDecoder.decode(id, "UTF-8"));
        List<Episode> episodes = episodesService.getAllEpisodes(season);
        return new MyRenderJson(episodes);
    }

    public Render acquire(String id, Boolean v) {
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        seasonsService.acquireSeason(id, v);
        return renderSuccess();
    }

    public Render watch(String id, Boolean v) {
        SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);
        seasonsService.watchSeason(id, v);
        return renderSuccess();
    }
}
