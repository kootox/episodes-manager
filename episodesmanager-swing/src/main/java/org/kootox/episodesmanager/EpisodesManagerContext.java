/*
 * #%L
 * episodesmanager-swing
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2009 - 2010 Jean Couteau
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.kootox.episodesmanager;

import com.google.common.base.Supplier;
import jaxx.runtime.JAXXUtil;
import org.kootox.episodesmanager.content.EpisodesListHandler;
import org.kootox.episodesmanager.content.TimeSpentUI;
import org.kootox.episodesmanager.content.TimeSpentHandler;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.ui.EpisodesManagerMainUI;
import jaxx.runtime.context.JAXXContextEntryDef;
import jaxx.runtime.decorator.DecoratorProvider;
import org.kootox.episodesmanager.content.EpisodesListUI;
import org.kootox.episodesmanager.ui.admin.AdminShowsUI;
import org.kootox.episodesmanager.ui.admin.SearchTVRageUI;
import org.kootox.episodesmanager.ui.admin.SearchTVRageResultsUI;

public class EpisodesManagerContext extends jaxx.runtime.context.DefaultApplicationContext {

    /**
     * the jaxx context entry to store the config
     */
    public static final JAXXContextEntryDef<EpisodesManagerConfig> CONFIG_ENTRY_DEF =
            JAXXUtil.newContextEntryDef(EpisodesManagerConfig.class);

    /**
     * the jaxx context entry to store the root context supplier
     */
    public static final JAXXContextEntryDef<Supplier> ROOTCONTEXT_SUPPLIER_ENTRY_DEF =
            JAXXUtil.newContextEntryDef(Supplier.class);

    public static final JAXXContextEntryDef<ServiceContext> SERVICECONTEXT_ENTRY_DEF =
            JAXXUtil.newContextEntryDef(ServiceContext.class);

    public static final JAXXContextEntryDef<EpisodesManagerMainUI> MAIN_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("mainui", EpisodesManagerMainUI.class);
    public static final JAXXContextEntryDef<EpisodesListUI> EPISODES_LIST_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("listui", EpisodesListUI.class);
    public static final JAXXContextEntryDef<EpisodesListHandler> EPISODES_LIST_HANDLER_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("listhandler", EpisodesListHandler.class);
    public static final JAXXContextEntryDef<TimeSpentHandler> TIME_SPENT_HANDLER_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("timeSpentHandler", TimeSpentHandler.class);
    public static final JAXXContextEntryDef<TimeSpentUI> TIME_SPENT_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("timeSpentui", TimeSpentUI.class);
    public static final JAXXContextEntryDef<SearchTVRageUI> SEARCH_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("searchui", SearchTVRageUI.class);
    public static final JAXXContextEntryDef<SearchTVRageResultsUI> SEARCHRESULTS_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("searchresultsui", SearchTVRageResultsUI.class);
    public static final JAXXContextEntryDef<AdminShowsUI> ADMINSHOWS_UI_ENTRY_DEF =
            JAXXUtil.newContextEntryDef("adminShows", AdminShowsUI.class);

    public static final JAXXContextEntryDef<Boolean> ADMINSHOWS_VISIBLE =
            JAXXUtil.newContextEntryDef("adminShowsVisible", Boolean.class);
    public static final JAXXContextEntryDef<Boolean> SEARCHUI_VISIBLE =
            JAXXUtil.newContextEntryDef("searchUIVisible", Boolean.class);
    public static final JAXXContextEntryDef<Boolean> SEARCHRESULT_VISIBLE =
            JAXXUtil.newContextEntryDef("searchResultVisible", Boolean.class);
    public static final JAXXContextEntryDef<Boolean> TIMESPENT_VISIBLE =
            JAXXUtil.newContextEntryDef("timeSpentVisible", Boolean.class);

    /**
     * l'intance partagée accessible après un appel à la méthode
     * {@link #init()}
     */
    protected static EpisodesManagerContext instance;

    /**
     * @return <code>true</code> si le context a été initialisé via la méthode
     *         {@link #init()}, <ocde>false</code> autrement.
     */
    public static boolean isInit() {
        return instance != null;
    }

    /**
     * Permet l'initialisation du contexte applicatif et positionne
     * l'instance partagée.
     * <p/>
     * Note : Cette méthode ne peut être appelée qu'une seule fois.
     * @return l'instance partagée
     * @throws IllegalStateException si un contexte applicatif a déja été positionné.
     */
    public static synchronized EpisodesManagerContext init() throws IllegalStateException {
        if (isInit()) {
            throw new IllegalStateException("there is an already application context registred.");
        }
        instance = new EpisodesManagerContext();
        return instance;
    }

    /**
     * Récupération du contexte applicatif.
     *
     * @return l'instance partagé du contexte.
     * @throws IllegalStateException si le contexte n'a pas été initialisé via
     *                               la méthode {@link #init()}
     */
    public static EpisodesManagerContext get() throws IllegalStateException {
        if (!isInit()) {
            throw new IllegalStateException("no application context registred.");
        }
        return instance;
    }

    protected EpisodesManagerContext() {
        super();
    }

    /**
     * close the application's context.
     *
     * @throws java.lang.Exception if any pb while closing
     */
    public void close() throws Exception {

        // fermeture du context principal
        clear();
    }

    public DecoratorProvider getDecoratorProvider() {
        return getContextValue(DecoratorProvider.class);
    }

    public void setEpisodesManagerMainUI(EpisodesManagerMainUI ui) {
        MAIN_UI_ENTRY_DEF.setContextValue(get(), ui);
    }

    public void setEpisodesListHandler(EpisodesListHandler handler) {
        EPISODES_LIST_HANDLER_ENTRY_DEF.setContextValue(get(), handler);
    }

    public void setTimeSpentHandler(TimeSpentHandler handler) {
        TIME_SPENT_HANDLER_ENTRY_DEF.setContextValue(get(), handler);
    }

    public static EpisodesManagerConfig getConfig() {
        return CONFIG_ENTRY_DEF.getContextValue(get());
    }

    public static ServiceContext getServiceContext() {
        return SERVICECONTEXT_ENTRY_DEF.getContextValue(get());
    }

}
