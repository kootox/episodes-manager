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
package org.kootox.episodesmanager.ui.systray;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.EpisodesManagerContext;
import org.kootox.episodesmanager.content.TimeSpentUI;
import org.kootox.episodesmanager.ui.EpisodesManagerMainUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import org.kootox.episodesmanager.ui.admin.AdminShowsUI;
import org.kootox.episodesmanager.ui.admin.SearchTVRageResultsUI;
import org.kootox.episodesmanager.ui.admin.SearchTVRageUI;

/**
 * User: couteau
 * Date: 13 mai 2010
 */
public class EpisodesTrayIcon {

    /**
     * Logger
     */
    private final static Log log = LogFactory.getLog(EpisodesTrayIcon.class);

    protected static EpisodesManagerContext context;

    /**
     * l'intance partagée accessible après un appel à la méthode
     * {@link #create()}
     */
    protected static Boolean loaded;

    protected static EpisodesTrayIcon instance;

    public EpisodesTrayIcon(){
    }

    public static void init(EpisodesManagerContext rootContext) {
        instance = new EpisodesTrayIcon();
        context = rootContext;
        loaded = false;
    }

    public static EpisodesTrayIcon get(){
        return instance;
    }

    public void create() {

        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            if (log.isInfoEnabled()){
                log.info("SystemTray is not supported");
            }
            return;
        }

        if (loaded){
            return;
        }
        
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage("systray.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem display = new MenuItem("Display");
        MenuItem exit = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(display);
        popup.addSeparator();
        popup.add(exit);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            if (log.isDebugEnabled()){
                log.debug("TrayIcon could not be added.");
            }
            return;
        }

        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1){
                    showHide();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //Do nothing
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //Do nothing
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //Do nothing
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                //Do nothing
            }
        });

        display.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHide();
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EpisodesManagerMainUI mainUI = EpisodesManagerContext.MAIN_UI_ENTRY_DEF.
                        getContextValue(context);
                mainUI.close();
            }
        });

        loaded = true;
        if (log.isDebugEnabled()){
            log.debug("Systray loaded");
        }
    }

    protected void showHide(){

        EpisodesManagerMainUI mainUI = EpisodesManagerContext.MAIN_UI_ENTRY_DEF.
                getContextValue(context);



        if (mainUI.isVisible()) {
            mainUI.setVisible(false);

            Boolean adminUIVisible = EpisodesManagerContext.ADMINSHOWS_VISIBLE.
                    getContextValue(context);

            Boolean searchUIVisible = EpisodesManagerContext.SEARCHUI_VISIBLE.
                    getContextValue(context);

            Boolean searchResultUIVisible = EpisodesManagerContext.SEARCHRESULT_VISIBLE.
                    getContextValue(context);

            Boolean timeSpentUIVisible = EpisodesManagerContext.TIMESPENT_VISIBLE.
                    getContextValue(context);

            //If adminUI is visible, hide it.
            if (adminUIVisible != null && adminUIVisible){
                AdminShowsUI adminUI = EpisodesManagerContext.ADMINSHOWS_UI_ENTRY_DEF.
                        getContextValue(context);
                adminUI.setVisible(false);
            }

            //If searchUI is visible, hide it.
            if (searchUIVisible != null && searchUIVisible) {
                SearchTVRageUI searchUI = EpisodesManagerContext.SEARCH_UI_ENTRY_DEF.
                        getContextValue(context);
                searchUI.setVisible(false);
            }

            //If searchResultUI is visible, hide it.
            if (searchResultUIVisible != null && searchResultUIVisible){
                SearchTVRageResultsUI searchResultUI = EpisodesManagerContext.SEARCHRESULTS_UI_ENTRY_DEF.
                        getContextValue(context);
                searchResultUI.setVisible(false);
            }

            //If timeSpentUI is visible, hide it.
            if (timeSpentUIVisible != null && timeSpentUIVisible) {
                TimeSpentUI timeSpentUI = EpisodesManagerContext.TIME_SPENT_UI_ENTRY_DEF.
                        getContextValue(context);
                timeSpentUI.setVisible(false);
            }

        } else {
            mainUI.setVisible(true);

            Boolean adminUIVisible = EpisodesManagerContext.ADMINSHOWS_VISIBLE.
                    getContextValue(context);

            Boolean searchUIVisible = EpisodesManagerContext.SEARCHUI_VISIBLE.
                    getContextValue(context);

            Boolean searchResultUIVisible = EpisodesManagerContext.SEARCHRESULT_VISIBLE.
                    getContextValue(context);

            Boolean timeSpentVisible = EpisodesManagerContext.TIMESPENT_VISIBLE.
                    getContextValue(context);

            //If adminUI was visible, show it.
            if (adminUIVisible != null && adminUIVisible) {
                AdminShowsUI adminUI = EpisodesManagerContext.ADMINSHOWS_UI_ENTRY_DEF.
                        getContextValue(context);
                adminUI.setVisible(true);
            }

            //If searchUI was visible, show it.
            if (searchUIVisible != null && searchUIVisible) {
                SearchTVRageUI searchUI = EpisodesManagerContext.SEARCH_UI_ENTRY_DEF.
                        getContextValue(context);
                searchUI.setVisible(true);
            }

            //If searchResultUI was visible, show it.
            if (searchResultUIVisible != null && searchResultUIVisible) {
                SearchTVRageResultsUI searchTVRageResultsUI =
                        EpisodesManagerContext.SEARCHRESULTS_UI_ENTRY_DEF.
                        getContextValue(EpisodesManagerContext.get());
                searchTVRageResultsUI.setVisible(true);
                System.out.println("Set visible");
            }

            //If timeSpentUI was visible, show it.
            if (timeSpentVisible != null && timeSpentVisible) {
                TimeSpentUI timeSpentUI = EpisodesManagerContext.TIME_SPENT_UI_ENTRY_DEF.
                        getContextValue(context);
                timeSpentUI.setVisible(true);
            }


        }
    }

    //Obtain the image URL
    protected Image createImage(String path, String description) {
        URL imgURL = getClass().getResource(path);
        Image icon = null;
        if (imgURL != null) {
            icon = new ImageIcon(imgURL, description).getImage();
        } else {
            log.warn("Couldn't find file: " + path);
        }
        return icon;
    }
    
}
