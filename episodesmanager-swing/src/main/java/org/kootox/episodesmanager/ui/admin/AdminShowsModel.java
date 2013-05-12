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

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.entities.Season;
import org.kootox.episodesmanager.entities.Show;

import static org.nuiton.i18n.I18n._;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.services.shows.SeasonsService;
import org.kootox.episodesmanager.services.shows.ShowsService;

public class AdminShowsModel extends AbstractTreeTableModel {

    private List<String> columnNames;

    protected EpisodesService episodesService = EpisodesManagerHelper.newService(EpisodesService.class);

    protected SeasonsService seasonsService = EpisodesManagerHelper.newService(SeasonsService.class);

    protected ShowsService showsService = EpisodesManagerHelper.newService(ShowsService.class);

    public AdminShowsModel() {
        super("root");
        columnNames = new ArrayList<String>();
        columnNames.add(_("episodesmanager.admin.serie"));
        columnNames.add(_("episodesmanager.admin.number"));
        columnNames.add(_("episodesmanager.admin.airingDate"));
        columnNames.add(_("episodesmanager.admin.acquired"));
        columnNames.add(_("episodesmanager.admin.watched"));
        columnNames.add(_("episodesmanager.admin.ignored"));
    }

    public int getColumnCount()
    {
        return columnNames.size();
    }

    public Class getColumnClass(int column){

        Class clazz = null;

        switch (column) {
            case 0 :
                clazz = String.class;
                break;
            case 1 :
                clazz = String.class;
                break;
            case 2 :
                clazz = Date.class;
                break;
            case 3 :
                clazz = Boolean.class;
                break;
            case 4 :
                clazz = Boolean.class;
                break;
            case 5 :
                clazz = Boolean.class;
                break;
        }

        return clazz;
    }

    public String getColumnName(int column)
    {
        return columnNames.get(column);
    }

    public Object getValueAt(Object aObject, int aColumn)
    {
        Object object = null;

        if((aObject instanceof Show) && (aColumn==0))
        {
            object = ((Show) aObject).getTitle();
        }

        if(aObject instanceof Season)
        {
            SeasonsService service = EpisodesManagerHelper.newService(SeasonsService.class);
            switch(aColumn)
            {
                case 0:
                    object = "Season" + ((Season) aObject).getNumber();
                    break;
                case 3:
                    object = service.getSeasonAcquired((Season)aObject);
                    break;
                case 4:
                    object = service.getSeasonWatched((Season)aObject);
                    break;
                case 5:
                    object = service.getSeasonIgnored((Season)aObject);
                    break;
            }

        }

        if(aObject instanceof Episode)
        {
            switch(aColumn)
            {
                case 0:
                    object = ((Episode) aObject).getTitle();
                    break;
                case 1:
                    int seasonNumber = ((Episode)aObject).getSeason().getNumber();
                    int episodeNumber = ((Episode) aObject).getNumber();
                    if (episodeNumber<10){
                        object = seasonNumber + "x0" + episodeNumber;
                    } else {
                        object = seasonNumber + "x" + episodeNumber;
                    }
                    break;
                case 2:
                    object = ((Episode) aObject).getAiringDate();
                    break;
                case 3:
                    object = ((Episode) aObject).getAcquired();
                    break;
                case 4:
                    object = ((Episode) aObject).getViewed();
                    break;
                case 5:
                    object = ((Episode) aObject).getIgnored();
                    break;
            }

        }
        return object;
    }

    @Override
    public int getChildCount(Object o) {

        int count = 0;


        if (o == getRoot()){
            count = showsService.getAllShows().size();
        }

        if (o instanceof Show) {
            count = seasonsService.getSeasonsCount((Show)o);
        }

        if (o instanceof Season) {
            count = episodesService.getEpisodesCount((Season)o);
        }

        return count;

    }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Object getChild(Object parent, int index) {

        Object returnObject = null;

        if (parent == getRoot()){

            List<Show> series = showsService.getAllShows();
            returnObject = series.get(index);
        }

        if (parent instanceof Show) {

            List<Season> seasons = seasonsService.getAllSeasons((Show)parent);
            returnObject = seasons.get(index);
        }

        if (parent instanceof Season) {

            List<Episode> episodes = episodesService.getAllEpisodes((Season)parent);
            returnObject = episodes.get(index);
        }

        return returnObject;
    }

    public boolean isCellEditable (Object node, int column) {
        return ((node instanceof Episode) || (node instanceof Season)) && ((column == 3) || (column == 4) || (column == 5));
    }

    public void setValueAt(Object value, Object node, int column) {

        if ((node instanceof Episode) && (column == 3)) {
            ((Episode) node).setAcquired((Boolean)value);
            if (!(Boolean)value){
                ((Episode) node).setViewed(false);
            }
            episodesService.updateEpisode((Episode)node);
        }

        if ((node instanceof Episode) && (column == 4)) {
            ((Episode) node).setViewed((Boolean)value);
            if ((Boolean)value){
                ((Episode) node).setAcquired(true);
            }
            episodesService.updateEpisode((Episode)node);
        }

        if ((node instanceof Episode) && (column == 5)) {
            ((Episode) node).setIgnored((Boolean)value);
            if ((Boolean)value){
                ((Episode) node).setIgnored(true);
            }
            episodesService.updateEpisode((Episode)node);
        }

        if ((node instanceof Season) && (column == 3)) {
            if ((Boolean) value) {
                seasonsService.setSeasonStatus((Season) node, true, null);
            } else {
                seasonsService.setSeasonStatus((Season) node, false, false);
            }
            for (int i=0; i<getChildCount(node); i++){
                //Manually create path to season to avoid useless recursions
                TreePath path = new TreePath(root);
                path = path.pathByAddingChild(((Season) node).getShow());
                path = path.pathByAddingChild(node);
                updateChildren(getChild(node,i),path);
            }
        }

        if ((node instanceof Season) && (column == 4)) {
            if (!(Boolean) value) {
                seasonsService.setSeasonStatus((Season) node, null, false);
            } else {
                seasonsService.setSeasonStatus((Season) node, true, true);
            }
            for (int i=0; i<getChildCount(node);i++){
                //Manually create path to season to avoid useless recursions
                TreePath path = new TreePath(root);
                path = path.pathByAddingChild(((Season) node).getShow());
                path = path.pathByAddingChild(node);
                updateChildren(getChild(node,i),path);
            }
        }

        if ((node instanceof Season) && (column == 5)) {
            if (!(Boolean) value) {
                seasonsService.setSeasonIgnored((Season) node,false);
            } else {
                seasonsService.setSeasonIgnored((Season) node, true);
            }
            for (int i=0; i<getChildCount(node);i++){
                //Manually create path to season to avoid useless recursions
                TreePath path = new TreePath(root);
                path = path.pathByAddingChild(((Season) node).getShow());
                path = path.pathByAddingChild(node);
                updateChildren(getChild(node,i),path);
            }
        }
    }

    /**
     * Recursively try to update node.
     *
     * @param node the node to update
     * @param path current path
     * @return <tt>true</tt> if children have been found and updated in current recursion
     */
    protected boolean updateChildren(Object node, TreePath path) {
        Object pathLastComponent = path.getLastPathComponent();

        // pour ses enfants
        boolean updated = false;
        int childCount = getChildCount(pathLastComponent);
        for (int childIndex = 0; childIndex < childCount; ++childIndex) {
            Object nodeUO = getChild(pathLastComponent, childIndex);

            TreePath childTreePath = path.pathByAddingChild(nodeUO);

            if (node.equals(nodeUO)) {
                modelSupport.fireChildChanged(path, childIndex, nodeUO);
                updateChildren(node, childTreePath);
                updated = true;
            } else {
                updated = updateChildren(node, childTreePath);

                if (updated) {
                    // ...and by recursion update all path
                    modelSupport.firePathChanged(path);
                }
            }
        }

        return updated;
    }
}
