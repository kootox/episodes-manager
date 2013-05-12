package org.kootox.episodesmanager.services.shows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.AbstractEpisodesManagerServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class TestEpisodesService extends AbstractEpisodesManagerServiceTest {

    protected EpisodesService service;

    @Before
    public void setUpService() {
        service = newService(EpisodesService.class);
    }

    @Test
    public void testGetAllEpisodes() throws Exception {
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();
        Episode sopranosSeason1Episode2 = fixtures.sopranosSeason1Episode2();
        Episode sopranosSeason2Episode1 = fixtures.sopranosSeason2Episode1();
        Episode sopranosSeason2Episode2 = fixtures.sopranosSeason2Episode2();

        List<Episode> episodes = service.getAllEpisodes();

        assertEquals(sopranosSeason1Episode1.getTitle(), episodes.get(0).getTitle());
        assertEquals(sopranosSeason1Episode2.getTitle(), episodes.get(1).getTitle());
        assertEquals(sopranosSeason2Episode1.getTitle(), episodes.get(2).getTitle());
        assertEquals(sopranosSeason2Episode2.getTitle(), episodes.get(3).getTitle());

    }

    @Test
    public void testGetEpisodeBy() throws Exception {
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();

        //test if normal use works
        assertEquals(sopranosSeason1Episode1.getTitle(),
                service.getEpisodeByNumber(sopranosSeason1, 1).getTitle());
        //test return null if episode does not exist
        assertNull(service.getEpisodeByNumber(sopranosSeason1, 25));
    }

    @Test
    public void testEpisodeCreation() throws Exception {
        //Create data into database
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        fixtures.sopranosSeason1Episode1();

        //Try the second method : createEpisode (season,episodeTitle)
        try {
            //try create an episode that already exist
            service.createEpisode(sopranosSeason1, 1, "Episode 1");
            fail("Should have thrown an exception");
        } catch (AlreadyExistException aee) {
        }
        //test the correct use of method
        service.createEpisode(sopranosSeason1, 4, "Episode 4");
        assertTrue(service.episodeExistsByNumber(sopranosSeason1, 4));
    }

    @Test
    public void testEpisodeDeleteByTopiaId() throws Exception {
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();

        assertEquals(1, service.getAllEpisodes().size());

        service.deleteEpisode(sopranosSeason1Episode1.getTopiaId());
        assertNull(service.getEpisodeByNumber(sopranosSeason1, 1));

        assertEquals(0, service.getAllEpisodes().size());
    }

    @Test
    public void testEpisodeDeletion() throws Exception {
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();

        //assertEquals(1, service.getAllEpisodes().size());

        service.deleteEpisode(sopranosSeason1Episode1);
        assertNull(service.getEpisodeByNumber(sopranosSeason1, 1));

        //assertEquals(0, service.getAllEpisodes().size());
    }

    @Test
    public void testGetToWatchEpisodes() throws Exception {
        Show sopranos = fixtures.sopranos();
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Episode sopranosSeason1Episode1 = fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();
        fixtures.sopranosSeason2Episode1();
        fixtures.sopranosSeason2Episode2();
        fixtures.sixFeetUnderSeason1Episode1();

        List<Episode> episodes = service.getToWatchEpisodes();

        assertEquals(1, episodes.size());
        Episode episode = (Episode) episodes.get(0);
        assertEquals(sopranosSeason1Episode1, episode);
        assertEquals(sopranosSeason1Episode1.getTitle(), episode.getTitle());
//        assertEquals(sopranosSeason1.getNumber(), episodes.get(0)[1]);
//        assertEquals(sopranos.getTitle(), episodes.get(0)[2]);
    }

    @Test
    public void testGetToAcquireEpisodes() throws Exception {
        Show sopranos = fixtures.sopranos();
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        fixtures.sopranosSeason1Episode1();
        Episode sopranosSeason1Episode2 = fixtures.sopranosSeason1Episode2();
        fixtures.sopranosSeason2Episode1();
        fixtures.sopranosSeason2Episode2();
        fixtures.sixFeetUnderSeason1Episode1();

        List<Episode> episodes = service.getToAcquireEpisodes();

        assertEquals(1, episodes.size());
        assertEquals(sopranosSeason1Episode2, episodes.get(0));
        assertEquals(sopranosSeason1Episode2.getTitle(), episodes.get(0).getTitle());
        assertEquals(sopranosSeason1.getNumber(), episodes.get(0).getSeason().getNumber());
        assertEquals(sopranos.getTitle(), episodes.get(0).getSeason().getShow().getTitle());
    }

    @Test
    public void testGetEpisodesCount() throws Exception {
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();

        assertEquals(2, service.getEpisodesCount(sopranosSeason1));
        assertEquals(0, service.getEpisodesCount(null));
    }
}
