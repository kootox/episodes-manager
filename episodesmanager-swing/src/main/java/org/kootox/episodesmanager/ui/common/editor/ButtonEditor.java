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
package org.kootox.episodesmanager.ui.common.editor;

import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.content.EpisodesListTableModel;
import org.kootox.episodesmanager.entities.Episode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.kootox.episodesmanager.services.shows.EpisodesService;

public class ButtonEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;
    private static final long serialVersionUID = -3422476392029957062L;
    private int row;
    private EpisodesListTableModel model;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        if (value == null) {
            label = "";
            JLabel returnLabel = new JLabel("");
            returnLabel.setBackground(table.getSelectionBackground());
            returnLabel.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            returnLabel.setOpaque(true);
            return returnLabel;
        } else {
            label = value.toString();
            button.setText(label);
            button.setBackground(table.getSelectionBackground());
            button.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            isPushed = true;
            this.row = row;
            this.model = (EpisodesListTableModel) table.getModel();
            return button;
        }
    }

    public Object getCellEditorValue() {
        if (isPushed)  {
            EpisodesService service = EpisodesManagerHelper.newService(EpisodesService.class);
            Episode episode = model.getEpisode(row);
            if (episode !=null) {
                if ((episode.getAcquired())&&(!episode.getViewed())){
                    episode.setViewed(true);
                }
                if (!episode.getAcquired()){
                    episode.setAcquired(true);
                }
                service.updateEpisode(episode);
            }
            model.update();
        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}

