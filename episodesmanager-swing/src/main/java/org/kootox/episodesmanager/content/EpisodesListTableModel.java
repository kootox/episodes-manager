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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.entities.Episode;
import org.kootox.episodesmanager.services.shows.EpisodesService;
import org.kootox.episodesmanager.ui.common.editor.ButtonEditor;
import org.kootox.episodesmanager.ui.common.renderer.ButtonRenderer;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.nuiton.i18n.I18n._;

/**
 *
 * @author couteau
 */
public class EpisodesListTableModel extends AbstractTableModel {

    /** Logger */
    private final static Log log = LogFactory.getLog(EpisodesListTableModel.class);
    protected List<Episode> oneToWatch;
    protected List<Episode> oneToAcquire;
    protected List<Episode> oneFuture;
    protected List<String> columnNames;
    protected ImageIcon acquireIcon = createImageIcon("dl.png", "The icon to acquire episode");
    protected ImageIcon watchIcon = createImageIcon("watch_eye.png", "The icon to watch episode");
    protected ImageIcon broadcastIcon = createImageIcon("calendar.png", "The icon to broadcast episode");
    protected EpisodesListUI ui;
    private static final long serialVersionUID = -4294962872952627958L;

    public EpisodesListTableModel(EpisodesListUI ui) {

        this.ui = ui;

        columnNames = new ArrayList<String>();
        columnNames.add(_("episodesmanager.episode.serie"));
        columnNames.add(_("episodesmanager.episode.number"));
        columnNames.add(_("episodesmanager.episode.title"));
        columnNames.add(_("episodesmanager.episode.status"));
        columnNames.add(_("episodesmanager.episode.airingDate"));
        columnNames.add(_("episodesmanager.episode.actions"));

        initValues();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Boolean returnBool = Boolean.FALSE;
        if (columnIndex==5) {
            returnBool = Boolean.TRUE;
        }
        return returnBool;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public int getRowCount() {
        return oneToAcquire.size() + oneToWatch.size() + oneFuture.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

        Object returnObject = null;

        Object[] displayRow; //the row to be displayed

        String status=null; //the status

        ImageIcon icon;//the icon

        //Get the row to display
        if (row<oneToAcquire.size()){
            Episode toAcquire = oneToAcquire.get(row);
            displayRow = new Object[]{toAcquire,
                toAcquire.getSeason().getNumber(),
                toAcquire.getSeason().getShow().getTitle()};
            status = "Acquire";
            icon = acquireIcon;
        } else if (row<oneToAcquire.size()+oneToWatch.size()){
            Episode toWatch = oneToWatch.get(row - oneToAcquire.size());
            displayRow = new Object[]{toWatch,
                toWatch.getSeason().getNumber(),
                toWatch.getSeason().getShow().getTitle()};
            status = "Watch";
            icon = watchIcon;
        } else {
            Episode coming = oneFuture.get(row - oneToAcquire.size() - oneToWatch.size());
            displayRow = new Object[]{coming,
                coming.getSeason().getNumber(),
                coming.getSeason().getShow().getTitle()};
            icon = broadcastIcon;
        }
        switch (col) {
            case 0 :
                returnObject = displayRow[2];
                break;
            case 1 :
                String episodeNumber = Integer.toString(((Episode) displayRow[0]).getNumber());
                if (episodeNumber.length()==1) {
                    returnObject = displayRow[1] + "x0" + episodeNumber;
                } else {
                    returnObject = displayRow[1] + "x" + episodeNumber;
                }
                break;
            case 2 :
                returnObject = ((Episode)displayRow[0]).getTitle();
                break;
            case 3 :
                returnObject = icon;
                break;
            case 4 :
                returnObject = ((Episode)displayRow[0]).getAiringDate();
                break;
            case 5 :
                returnObject = status;
                break;
        }
        return returnObject;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col==5){
            fireTableCellUpdated(row, col);
        }
    }

    public void update() {

        initValues();
        fireTableDataChanged();
        if (ui!=null){
        JTable table = ui.getEpisodeslistTable();
        TableColumn column = table.getColumn(_("episodesmanager.episode.actions"));
        column.setCellRenderer(new ButtonRenderer());
        column.setCellEditor(new ButtonEditor(new JCheckBox()));
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the icon
     * @param description the description of the icon
     * @return the icon
     */
    protected ImageIcon createImageIcon(String path, String description) {
        URL imgURL = getClass().getResource(path);
        ImageIcon icon = null;
        if (imgURL != null) {
            icon =  new ImageIcon(imgURL, description);
        } else {
            log.warn("Couldn't find file: " + path);
        }
        return icon;
    }

    public Episode getEpisode(int row){
        Episode episode;
        if (row < oneToAcquire.size()){
            episode = oneToAcquire.get(row);
        } else if (row < oneToAcquire.size() + oneToWatch.size()) {
            episode = oneToWatch.get(row-oneToAcquire.size());
        } else {
            episode = oneFuture.get(row-oneToAcquire.size()-oneToWatch.size());
        }
        return episode;
    }

    protected void initValues(){

        EpisodesService service = EpisodesManagerHelper.newService(EpisodesService.class);

        oneToAcquire = service.getToAcquireEpisodes();
        oneToWatch = service.getToWatchEpisodes();
        oneFuture = service.getFutureEpisodes();

    }

}
