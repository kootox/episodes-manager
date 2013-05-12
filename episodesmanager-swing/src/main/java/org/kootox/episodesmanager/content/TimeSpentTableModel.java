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

import org.kootox.episodesmanager.services.EpisodesManagerHelper;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import org.kootox.episodesmanager.services.shows.EpisodesService;

import static org.nuiton.i18n.I18n._;

/**
 * User: couteau
 * Date: 3 août 2010
 */
public class TimeSpentTableModel extends AbstractTableModel {

    protected List<Object[]> data;
    protected List<String> columnNames;
    protected TimeSpentUI ui;
    protected long total;

    public TimeSpentTableModel(TimeSpentUI ui) {

        this.ui = ui;

        columnNames = new ArrayList<String>();
        columnNames.add(_("episodesmanager.episode.serie"));
        columnNames.add(_("episodesmanager.show.status"));
        columnNames.add(_("episodesmanager.show.runtime"));
        columnNames.add(_("episodesmanager.show.episodes.watched"));
        columnNames.add(_("episodesmanager.timespent.minutes"));
        columnNames.add(_("episodesmanager.timespent.days"));

        initValues();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return Boolean.FALSE;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public int getRowCount() {
        return data.size()+1;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

        Object returnObject;

        if (row==data.size()){
            switch (col){
                case 1:
                    returnObject = "Total";
                    break;
                case 4:
                    returnObject = total;
                    break;
                case 5:
                    returnObject = EpisodesManagerHelper.convertIntoDays(total);
                    break;
                default:
                    returnObject="";
                    break;
            }
        } else {

            switch (col){
                case 5 :
                    Long minutes = (Long) data.get(row)[4];
                    if (minutes != null) {
                        returnObject = EpisodesManagerHelper.convertIntoDays(minutes);
                    } else {
                        returnObject = "";
                    }
                    break;
                case 1 :
                    if ((Boolean)data.get(row)[1]){
                        returnObject = _("episodesmanager.show.ended");
                    } else {
                        returnObject = _("episodesmanager.show.running");
                    }
                    break;
                default :
                    returnObject = data.get(row)[col];
                    break;
            }
        }

        return returnObject;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //Do nothing
    }

    public void update() {

        initValues();
        fireTableDataChanged();
    }

    private void initValues() {

        EpisodesService service = EpisodesManagerHelper.newService(EpisodesService.class);

        this.data = service.getTimeSpentTable();

        //Total time spent calculation
        total = 0;

        for(Object[] row:data){
            if (row[4]!=null){
                long rowTime = (Long) row[4];
                total = total + rowTime;
            }
        }

    }



}

