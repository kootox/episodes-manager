package org.kootox.episodesmanager.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.exceptions.AlreadyExistException;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;
import org.nuiton.util.DateUtil;

/**
 * Le but de ces classes est de fournir des échantillons de données à utiliser
 * pour mettre la base dans un état déterminé au début d'un test.
 *
 * Chaque méthode, charge en base une entité, et au besoin, toutes les
 * dépendances.
 *
 * @author jcouteau <couteau@codelutin.com>
 */
public class FixturesService implements EpisodesManagerService {

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(FixturesService.class);

    protected FakeServiceContext serviceContext;

    protected Show sopranos;

    protected Season sopranosSeason1;

    protected Season sopranosSeason2;

    protected Episode sopranosSeason1Episode1;

    protected Episode sopranosSeason1Episode2;

    protected Episode sopranosSeason2Episode1;

    protected Episode sopranosSeason2Episode2;

    protected Show sixFeetUnder;

    protected Season sixFeetUnderSeason1;

    protected Episode sixFeetUnderSeason1Episode1;

    @Override
    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = (FakeServiceContext)serviceContext;
    }

    public Show sopranos() {
        if (sopranos == null) {
            ShowsService showsService = serviceContext.newService(ShowsService.class);
            try {
                sopranos  = showsService.createShow("The Sopranos");
                sopranos.setIgnored(false);
                showsService.updateShow(sopranos);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos\" already exists");
                }
            }
        }
        return sopranos;
    }

    public Show sixFeetUnder() {
        if (sixFeetUnder == null) {
            ShowsService showsService = serviceContext.newService(ShowsService.class);
            try {
                sixFeetUnder = showsService.createShow("Six Feet Under");
                sixFeetUnder.setIgnored(false);
                showsService.updateShow(sixFeetUnder);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"Six Feet Under\" already exists");
                }
            }
        }
        return sixFeetUnder;
    }

    public Season sopranosSeason1() {
        if (sopranosSeason1 == null) {
            Show sopranos = sopranos();
            SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);
            try {
                sopranosSeason1 = seasonsService.createSeason(sopranos, 1);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 1\" already exists");
                }
            }
        }
        return  sopranosSeason1;
    }

    public Season sopranosSeason2() {
        if (sopranosSeason2 == null) {
            Show sopranos = sopranos();
            SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);
            try {
                sopranosSeason2 = seasonsService.createSeason(sopranos, 2);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 2\" already exists");
                }
            }
        }
        return  sopranosSeason2;
    }

    public Episode sopranosSeason1Episode1() {
        if (sopranosSeason1Episode1 == null) {
            Season season1 = sopranosSeason1();
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            try {
                sopranosSeason1Episode1 = episodesService.createEpisode(season1, 1, "Episode 1");
                sopranosSeason1Episode1.setAcquired(true);
                sopranosSeason1Episode1.setIgnored(false);
                sopranosSeason1Episode1.setAiringDate(DateUtil.createDate(1,10,1986));
                episodesService.updateEpisode(sopranosSeason1Episode1);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 1 - Episode 1\" already exists");
                }
            }
        }
        return sopranosSeason1Episode1;
    }

    public Episode sopranosSeason1Episode2() {
        if (sopranosSeason1Episode2 == null) {
            Season season1 = sopranosSeason1();
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            try {
                sopranosSeason1Episode2 = episodesService.createEpisode(season1, 2, "Episode 2");
                sopranosSeason1Episode2.setAiringDate(DateUtil.createDate(1,10,1986));
                sopranosSeason1Episode2.setIgnored(false);
                episodesService.updateEpisode(sopranosSeason1Episode2);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 1 - Episode 2\" already exists");
                }
            }
        }
        return sopranosSeason1Episode2;
    }

    public Episode sopranosSeason2Episode1() {
        if (sopranosSeason2Episode1 == null) {
            Season season2 = sopranosSeason2();
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            try {
                sopranosSeason2Episode1 = episodesService.createEpisode(season2, 1, "Episode 1");
                sopranosSeason2Episode1.setAiringDate(DateUtil.createDate(1,10,1990));
                sopranosSeason2Episode1.setAcquired(true);
                sopranosSeason2Episode1.setViewed(false);
                sopranosSeason2Episode1.setIgnored(false);
                episodesService.updateEpisode(sopranosSeason2Episode1);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 2 - Episode 1\" already exists");
                }
            }
        }
        return sopranosSeason2Episode1;
    }

    public Episode sopranosSeason2Episode2() {
        if (sopranosSeason2Episode2 == null) {
            Season season2 = sopranosSeason2();
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            try {
                sopranosSeason2Episode2 = episodesService.createEpisode(season2, 2, "Episode 2");
                sopranosSeason2Episode2.setAiringDate(DateUtil.createDate(2,10,1990));
                sopranosSeason2Episode2.setAcquired(true);
                sopranosSeason2Episode2.setViewed(true);
                sopranosSeason2Episode2.setIgnored(false);
                episodesService.updateEpisode(sopranosSeason2Episode2);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"The Sopranos - Season 2 - Episode 2\" already exists");
                }
            }
        }
        return sopranosSeason2Episode2;
    }

    public Season sixFeetUnderSeason1() {
        if (sixFeetUnderSeason1 == null) {
            Show sixFeetUnder = sixFeetUnder();
            SeasonsService seasonsService = serviceContext.newService(SeasonsService.class);
            try {
                sixFeetUnderSeason1 = seasonsService.createSeason(sixFeetUnder, 1);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"Six Feet Under - Season 1\" already exists");
                }
            }
        }
        return sixFeetUnderSeason1;
    }

    public Episode sixFeetUnderSeason1Episode1() {
        if (sixFeetUnderSeason1Episode1 == null) {
            Season season1 = sixFeetUnderSeason1();
            EpisodesService episodesService = serviceContext.newService(EpisodesService.class);
            try {
                sixFeetUnderSeason1Episode1 = episodesService.createEpisode(season1, 1, "Episode 1");
                sixFeetUnderSeason1Episode1.setAiringDate(DateUtil.createDate(1,10,1986));
                sixFeetUnderSeason1Episode1.setAcquired(true);
                sixFeetUnderSeason1Episode1.setIgnored(true);
                episodesService.updateEpisode(sixFeetUnderSeason1Episode1);
            } catch (AlreadyExistException eee) {
                if (log.isDebugEnabled()) {
                    log.debug("\"Six Feet Under - Season 1 - Episode 1\" already exists");
                }
            }
        }
        return sixFeetUnderSeason1Episode1;
    }
}
