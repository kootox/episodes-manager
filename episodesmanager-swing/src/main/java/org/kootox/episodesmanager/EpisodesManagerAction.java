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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuiton.i18n.I18n;

@jaxx.runtime.context.DefaultApplicationContext.AutoLoad
public class EpisodesManagerAction {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(EpisodesManagerAction.class);
    /**
     * La configuration de l'application.
     */
    protected EpisodesManagerConfig config;

    public EpisodesManagerAction() {
    }

    public EpisodesManagerAction(EpisodesManagerConfig config) {
        this.config = config;
    }

    /*public void configure() {
        disableMainUI();
    }

    public void help() {
        disableMainUI();

        System.out.println(I18n._("episodesmanager.message.help.usage", getConfig().getVersion()));
        System.out.println("Options (set with --option <key> <value>:");
        for (EpisodesManagerConfig.Option o : EpisodesManagerConfig.Option.values()) {
            System.out.println("\t" + o.key + "(" + o.defaultValue + "):" + o.description);
        }

        System.out.println("Actions:");
        for (EpisodesManagerConfig.Action a : EpisodesManagerConfig.Action.values()) {
            System.out.println("\t" + java.util.Arrays.toString(a.aliases) + "(" + a.action + "):" + a.description);
        }
        System.exit(0);
    }*/

    /**
     * Désactiver la possiblite de lancer l'ui principale.
     */
    /*public void disableMainUI() {
        if (log.isDebugEnabled()) {
            log.debug(this);
        }
        getConfig().setDisplayMainUI(false);
    }*/

    protected EpisodesManagerConfig getConfig() {
        if (config == null) {
            EpisodesManagerContext context = EpisodesManagerContext.get();
            config = context.getContextValue(EpisodesManagerConfig.class);
        }
        return config;
    }
}
