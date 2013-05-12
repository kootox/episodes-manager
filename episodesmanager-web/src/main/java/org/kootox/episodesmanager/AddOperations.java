package org.kootox.episodesmanager;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.services.databases.TheTvDbService;

import java.util.Map;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class AddOperations extends WebMotionController {

    public Render search() {
        return renderView("searchShow.jsp");
    }

    public Render searchShow(String query){

        TheTvDbService service = EpisodesManagerHelper.newService(TheTvDbService.class);

        Map<Integer,String> results = service.search(query,"fr");

        return renderView("searchShowResult.jsp",
                "q", query,
                "results", results);
    }

    public Render addShow(Integer id){

        TheTvDbService service = EpisodesManagerHelper.newService(TheTvDbService.class);

        service.createOrUpdate(id,"fr");

        return renderRedirect("/");
    }
}
