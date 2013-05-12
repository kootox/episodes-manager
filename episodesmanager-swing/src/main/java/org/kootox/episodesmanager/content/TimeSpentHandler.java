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
package org.kootox.episodesmanager.content;

import jaxx.runtime.JAXXContext;
import org.kootox.episodesmanager.EpisodesManagerContext;

/**
 * User: couteau
 * Date: 3 août 2010
 */
public class TimeSpentHandler {

    private TimeSpentTableModel model;

    /**
     * Methode pour initialiser l'ui principale sans l'afficher.
     *
     * @param context le context applicatif
     * @return l'ui instancie et initialisee mais non visible encore
     */
    public TimeSpentUI initUI(EpisodesManagerContext context) {

        // show main ui
        context.setTimeSpentHandler(this);
        TimeSpentUI ui = new TimeSpentUI(context);

        EpisodesManagerContext.TIME_SPENT_UI_ENTRY_DEF.setContextValue(context, ui);

        return ui;
    }

    public TimeSpentTableModel getTimeSpentTableModel() {

        if (null == model) {
            model = new TimeSpentTableModel(getUI(EpisodesManagerContext.get()));
        }
        return model;
    }

    TimeSpentUI getUI(JAXXContext context) {
        if (context instanceof TimeSpentUI) {
            return (TimeSpentUI) context;
        }
        return null;
    }

    public void updateUI() {
        model.update();
    }
}
