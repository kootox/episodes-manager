package org.kootox.episodesmanager.services.shows;

import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.AbstractEpisodesManagerServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class TestSeasonsService extends AbstractEpisodesManagerServiceTest {

    protected SeasonsService service;

    @Before
    public void setUpService() {
        service = newService(SeasonsService.class);
    }

    @Test
    public void testSeasonCreation() throws Exception {
        Show sopranos = fixtures.sopranos();

        service.createSeason(sopranos, 1);
        assertEquals(1, (int) service.getSeasonByNumber(sopranos, 1).getNumber());
        try {
            service.createSeason(sopranos, 1);
            fail("An error should have been thrown at this point");
        } catch (AlreadyExistException aee) {
        }

        Season nullSeason = service.createSeason(null, 3);
        assertNull(nullSeason);
    }

    @Test
    public void testSeasonDeletionByTopiaId() throws Exception {
        Show sopranos = fixtures.sopranos();
        Season sopranosSeason1 = fixtures.sopranosSeason1();

        service.deleteSeason(sopranosSeason1.getTopiaId());
        assertNull(service.getSeasonByNumber(sopranos, 1));
    }

    @Test
    public void testSeasonDeletion() throws Exception {
        Show sopranos = fixtures.sopranos();
        Season sopranosSeason1 = fixtures.sopranosSeason1();

        service.deleteSeason(sopranosSeason1);
        assertNull(service.getSeasonByNumber(sopranos, 1));
    }

    @Test
    public void testGetAllSeasons() throws Exception {
        Show sopranos = fixtures.sopranos();
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Season sopranosSeason2 = fixtures.sopranosSeason2();

        //try getAllSeasons(show)
        List<Season> seasons = service.getAllSeasons(sopranos);
        Assert.assertEquals(sopranosSeason1.getNumber(), seasons.get(0).getNumber());
        Assert.assertEquals(sopranosSeason2.getNumber(), seasons.get(1).getNumber());

        //try getAllSeasons()
        seasons = service.getAllSeasons();
        Assert.assertEquals(2, seasons.size());
        Assert.assertEquals(sopranosSeason1.getNumber(), seasons.get(0).getNumber());
        Assert.assertEquals(sopranosSeason2.getNumber(), seasons.get(1).getNumber());
    }

    @Test
    public void testGetSeasonBy() throws Exception {
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Show sopranos = fixtures.sopranos();

        //test if normal use works
        assertEquals(sopranosSeason1.getNumber(), service.getSeasonByNumber(sopranos, 1).getNumber());
        //test return null if episode does not exist
        assertNull(service.getSeasonByNumber(sopranos, 34));
    }

    @Test
    public void testGetSeasonAcquired() throws Exception {
        //Prepare data into database
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Season sopranosSeason2 = fixtures.sopranosSeason2();
        fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();
        fixtures.sopranosSeason2Episode1();
        fixtures.sopranosSeason2Episode2();

        assertFalse(service.getSeasonAcquired(sopranosSeason1));
        assertTrue(service.getSeasonAcquired(sopranosSeason2));
    }

    @Test
    public void testGetSeasonWatched() throws Exception {
        //Prepare data into database
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Season sopranosSeason2 = fixtures.sopranosSeason2();
        fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();
        fixtures.sopranosSeason2Episode1();
        fixtures.sopranosSeason2Episode2();

//        assertFalse(service.getSeasonWatched(sopranosSeason1));
//        assertTrue(service.getSeasonWatched(sopranosSeason2));
    }

    @Test
    public void testSetSeasonStatus() throws Exception {
        //Prepare data into database
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        fixtures.sopranosSeason1Episode1();
        fixtures.sopranosSeason1Episode2();

        assertFalse(service.getSeasonWatched(sopranosSeason1));
        assertFalse(service.getSeasonAcquired(sopranosSeason1));

        service.setSeasonStatus(sopranosSeason1, true, true);
        assertTrue(service.getSeasonWatched(sopranosSeason1));
        assertTrue(service.getSeasonAcquired(sopranosSeason1));
    }

    @Test
    public void testGetSeasonsCount() throws Exception {
        Show show = fixtures.sopranos();
        //Load seasons in database
        Season sopranosSeason1 = fixtures.sopranosSeason1();
        Season sopranosSeason2 = fixtures.sopranosSeason2();

        assertEquals(2, service.getSeasonsCount(show));

        assertEquals(0, service.getSeasonsCount(null));
    }
}
