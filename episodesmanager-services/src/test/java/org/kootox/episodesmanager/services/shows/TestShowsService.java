package org.kootox.episodesmanager.services.shows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.AbstractEpisodesManagerServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class TestShowsService extends AbstractEpisodesManagerServiceTest {

    protected ShowsService service;

    @Before
    public void setUpService() {
        service = newService(ShowsService.class);
    }

    @Test
    public void testCreateShow() {
        //Test first creation
        try {
            service.createShow("The Sopranos");
        } catch (AlreadyExistException eee) {
            fail("No error should have been thrown");
        }
        assertEquals("The Sopranos",
                service.getShowByName("The Sopranos").getTitle());

        //Test Exception is thrown when trying second creation
        try {
            service.createShow("The Sopranos");
            fail("An error should have been thrown at this point");
        } catch (AlreadyExistException aee) {

        }
    }

    @Test
    public void testShowUpdate() throws Exception {
        Show show = service.createShow("The Sopranos");
        show.setTitle("Toto");
        service.updateShow(show);

        assertEquals("Toto",
                service.getShowByName("Toto").getTitle());
    }

    @Test
    public void testShowDeletionByTopiaId() throws Exception {
        Show sopranos = fixtures.sopranos();
        service.deleteShow(sopranos.getTopiaId());
        assertNull(service.getShowByName(sopranos.getTitle()));
    }

    @Test
    public void testShowDeletion() throws Exception {
        Show sopranos = fixtures.sopranos();
        service.deleteShow(sopranos);
        assertNull(service.getShowByName(sopranos.getTitle()));
    }

    @Test
    public void testGetAllShows() throws Exception {

        Show sopranos = fixtures.sopranos();
        Show sixFeetUnder = fixtures.sixFeetUnder();

        List<Show> shows = service.getAllShows();

        assertEquals(sixFeetUnder.getTitle(), shows.get(0).getTitle());
        assertEquals(sopranos.getTitle(), shows.get(1).getTitle());
    }

    @Test
    public void testGetShowById() throws Exception {

        Show sopranos = fixtures.sopranos();

        Show show2 = service.getShowById(sopranos.getTopiaId());
        Show show3 = service.getShowById("wrongId");

        assertEquals(sopranos, show2);
        assertNull(show3);
    }



}
