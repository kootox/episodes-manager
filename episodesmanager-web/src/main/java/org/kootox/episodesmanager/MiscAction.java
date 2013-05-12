package org.kootox.episodesmanager;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.shows.EpisodesService;

import java.util.List;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class MiscAction extends WebMotionController {

    public Render timeSpent() {
        EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);
        List<Object[]> table = episodesService.getTimeSpentTable();
        return renderView("timeSpent.jsp",
                "table",table);
    }

}
