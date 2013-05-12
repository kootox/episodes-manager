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

        Long total = 0L;

        for (Object[] show:table) {
            Long showTime = (Long)show[4];
            total += showTime;
            show[4] = EpisodesManagerHelper.convertIntoDays(showTime);
        }

        String totalString = EpisodesManagerHelper.convertIntoDays(total);

        return renderView("timeSpent.jsp",
                "table", table,
                "total", totalString);
    }

}
