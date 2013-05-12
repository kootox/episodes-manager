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
package org.kootox.episodesmanager.ui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * User: couteau
 * Date: 27 mars 2010
 */
public class XmlFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);

        return extension != null && "xml".equals(extension);

    }

    @Override
    public String getDescription(){
        return "xml files";
    }

    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
