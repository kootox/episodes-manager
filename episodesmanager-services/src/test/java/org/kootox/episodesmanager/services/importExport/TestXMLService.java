package org.kootox.episodesmanager.services.importExport;

import java.io.File;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.services.AbstractEpisodesManagerServiceTest;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class TestXMLService extends AbstractEpisodesManagerServiceTest {


    protected XmlService service;

    @Before
    public void setUpService() {
        service = newService(XmlService.class);
    }

    @Test
    public void testXMLExportImport() throws Exception {

        EpisodesService episodesService = newService(EpisodesService.class);
        SeasonsService seasonsService = newService(SeasonsService.class);
        ShowsService showsService = newService(ShowsService.class);

        //Load episodes, seasons and show
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();
        fixtures.sopranosSeason2Episode1();
        fixtures.sopranosSeason2Episode2();

        service.exportToXML(new File("/tmp/test.xml"));

        //clear database
        for (Show show : showsService.getAllShows()) {
            showsService.deleteShow(show);
        }

        service.importFromXML(new File("/tmp/test.xml"));

        assertTrue(showsService.showExists("The Sopranos"));
        Show show = showsService.getShowByName("The Sopranos");
        assertTrue(seasonsService.seasonExists(show, 1));
        assertTrue(seasonsService.seasonExists(show, 2));

        Collection<Season> seasons = seasonsService.getAllSeasons(show);

        Season season1 = null;
        Season season2 = null;

        for (Season season : seasons) {
            if (season.getNumber() == 1) {
                season1 = season;
            }
            if (season.getNumber() == 2) {
                season2 = season;
            }
        }

        assertTrue(episodesService.episodeExistsByNumber(season1, 1));
        assertTrue(episodesService.episodeExistsByNumber(season1, 2));
        assertTrue(episodesService.episodeExistsByNumber(season2, 1));
        assertTrue(episodesService.episodeExistsByNumber(season2, 2));

        Episode episode = episodesService.getEpisodeByNumber(season1, 1);

        assertEquals(sopranosSeason1Episode1.getAiringDate(), episode.getAiringDate());
        assertEquals(sopranosSeason1Episode1.getTitle(), episode.getTitle());
        assertEquals(sopranosSeason1Episode1.getAcquired(), episode.getAcquired());
        assertEquals(sopranosSeason1Episode1.getViewed(), episode.getViewed());
    }

}
