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
package org.kootox.episodesmanager.ui.admin;

import javax.swing.JDialog;
import jaxx.runtime.JAXXContext;
import jaxx.runtime.context.JAXXInitialContext;
import org.kootox.episodesmanager.EpisodesManagerContext;
import org.kootox.episodesmanager.content.EpisodesListHandler;
import org.kootox.episodesmanager.content.EpisodesListTableModel;
import org.kootox.episodesmanager.content.EpisodesListUI;
import org.kootox.episodesmanager.ui.EpisodesManagerMainUIHandler;

/**
 * User: couteau
 * Date: 21 févr. 2010
 */
public class AdminShowsHandler {

    private JAXXInitialContext context;

    public AdminShowsUI initUI(JAXXContext rootContext, EpisodesManagerMainUIHandler rootHandler) {

        context = new JAXXInitialContext().add(rootContext);

        // show main ui
        context.add(this);

        return new AdminShowsUI(context);
    }

    SearchTVRageResultsUI getUI(JAXXContext context) {
        if (context instanceof SearchTVRageResultsUI) {
            return (SearchTVRageResultsUI) context;
        }
        return null;
    }

    public void updateMainUI(){

        EpisodesListUI episodesListUI = EpisodesManagerContext.EPISODES_LIST_UI_ENTRY_DEF.getContextValue(context);

        EpisodesListHandler handler = episodesListUI.getHandler();

        EpisodesListTableModel tableModel = handler.getEpisodesListTableModel();

        tableModel.update();

    }

    public void close(JDialog ui) {
        ui.dispose();
        EpisodesManagerContext.ADMINSHOWS_VISIBLE.
                setContextValue(EpisodesManagerContext.get(), false);
        updateMainUI();
    }
}
