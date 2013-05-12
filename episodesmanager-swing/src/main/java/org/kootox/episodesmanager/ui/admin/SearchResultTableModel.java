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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.nuiton.i18n.I18n._;

/**
 * Model of the SearchResultUI.
 * @author couteau
 */
public class SearchResultTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    /** Logger */
    private final static Log log = LogFactory.getLog(SearchResultTableModel.class);
    protected SearchTVRageResultsUI ui;
    protected List<String> columnNames;
    protected Map<Integer, String> data; //the id and name of all the shows to select between
    protected List<Boolean> bool; //the status of the shows : true to import, false do nothing.

    /**
     * Constructor from parent ui. Will not show any choice.
     * @param ui the parent ui.
     */
    public SearchResultTableModel(SearchTVRageResultsUI ui) {

        this.ui = ui;

        columnNames = new ArrayList<String>();
        columnNames.add(_("episodesmanager.search.select"));
        columnNames.add(_("episodesmanager.search.title"));
    }

    /**
     * Constructor from parent ui and a list of ids to select between.
     * @param ui the parent ui.
     * @param map the map containing the series to select between and their id.
     */
    public SearchResultTableModel(SearchTVRageResultsUI ui,
                                  Map<Integer, String> map) {

        this.data = map;
        this.bool = new ArrayList<Boolean>();
        for (int i = 0; i < data.size(); i++) {
            bool.add(Boolean.FALSE);
        }

        this.ui = ui;

        columnNames = new ArrayList<String>();
        columnNames.add(_("episodesmanager.search.select"));
        columnNames.add(_("episodesmanager.search.title"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Boolean returnBoolean = Boolean.FALSE;
        if (columnIndex == 0) {
            returnBoolean = Boolean.TRUE;
        }
        return returnBoolean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int row, int col) {

        Object returnValue = null;

        if (col == 0) {
            returnValue = bool.get(row);
        } else {
            Collection<String> values = data.values();

            int i = 0;

            for (String value : values) {
                if (row == i) {
                    returnValue = value;
                }
                i++;
            }
        }

        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            log.info("Select serie for import");
            bool.set(row, (Boolean) value);
        }
    }

    /**
     * Get all the ids that are selected for importation.
     * @return A List containing the ids of all the selected series.
     */
    public List<Integer> getSelectedId() {

        List<Integer> selected = new ArrayList<Integer>();

        Set<Integer> values = data.keySet();

        int i = 0;

        for (int value : values) {
            log.info("Is " + value + " selected ?");
            if (bool.get(i)) {
                log.info("Selected : " + value);
                selected.add(value);
            }
            i++;
        }
        return selected;
    }
}
