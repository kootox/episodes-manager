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
package org.kootox.episodesmanager.ui.common.renderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * User: couteau
 * Date: 31 janv. 2010
 */
public class CenteredLabelRenderer extends DefaultTableCellRenderer {


    public CenteredLabelRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        JLabel result = new JLabel((String)value);

        result.setOpaque(true);

        if ( row % 2 == 0 ) {
            result.setBackground(UIManager.getColor("Table.alternateRowColor"));
        } else {
            result.setBackground(Color.WHITE);
        }

        if (isSelected) {
            result.setBackground(table.getSelectionBackground());
        }
        if (hasFocus) {
            result.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        }

        result.setHorizontalAlignment(SwingConstants.CENTER);

        return result;
    }
}
