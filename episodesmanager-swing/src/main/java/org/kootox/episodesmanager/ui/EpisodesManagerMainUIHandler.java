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

import jaxx.runtime.JAXXContext;
import jaxx.runtime.SwingUtil;
import jaxx.runtime.context.DefaultApplicationContext.AutoLoad;
import jaxx.runtime.context.JAXXInitialContext;
import jaxx.runtime.decorator.DecoratorProvider;
import jaxx.runtime.swing.AboutPanel;
import jaxx.runtime.swing.editor.config.ConfigUIHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.kootox.episodesmanager.EpisodesManagerConfigOption;
import org.kootox.episodesmanager.EpisodesManagerContext;
import org.kootox.episodesmanager.services.EpisodesManagerHelper;
import org.kootox.episodesmanager.EpisodesManagerRunner;

import org.kootox.episodesmanager.content.EpisodesListHandler;
import org.kootox.episodesmanager.content.EpisodesListTableModel;
import org.kootox.episodesmanager.content.EpisodesListUI;
import org.kootox.episodesmanager.content.TimeSpentHandler;
import org.kootox.episodesmanager.content.TimeSpentUI;
import org.kootox.episodesmanager.entities.Show;
import org.kootox.episodesmanager.services.databases.TheTvDbService;
import org.kootox.episodesmanager.services.importExport.CsvService;
import org.kootox.episodesmanager.services.importExport.XmlService;
import org.kootox.episodesmanager.services.shows.ShowsService;
import org.kootox.episodesmanager.ui.admin.AdminShowsUI;
import org.kootox.episodesmanager.ui.admin.AdminShowsHandler;
import org.kootox.episodesmanager.ui.admin.SearchResultHandler;
import org.kootox.episodesmanager.ui.admin.SearchTVRageUI;
import org.kootox.episodesmanager.ui.admin.SearchTVRageResultsUI;
import org.kootox.episodesmanager.ui.systray.EpisodesTrayIcon;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.util.Resource;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static org.nuiton.i18n.I18n._;
import static org.nuiton.i18n.I18n.n_;

/**
 * Main UI handler
 *
 * @author kootox
 * @see EpisodesManagerMainUI
 */
@AutoLoad
public class EpisodesManagerMainUIHandler { //implements JAXXHelp {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(EpisodesManagerMainUIHandler.class);

    private EpisodesListHandler episodesListHandler = new EpisodesListHandler();

    //Common file chooser to keep directories between use.
    private JFileChooser fc = new JFileChooser();

    private Timer timer = null;
    private TimerTask updateTask = null;

    /**
     * Methode pour initialiser l'ui principale sans l'afficher.
     *
     * @return l'ui instancie et initialisee mais non visible encore
     */
    public EpisodesManagerMainUI initUI() {

        EpisodesManagerContext context = EpisodesManagerContext.get();
        EpisodesManagerConfig config = EpisodesManagerContext.getConfig();

        if (!config.getLocale().equals(
                I18n.getStore().getCurrentLocale())) {
            if (log.isInfoEnabled()) {
                log.info("re-init I18n with locale " + config.getLocale());
            }
            I18n.init(new DefaultI18nInitializer("episodesmanager-swing"), config.getLocale());
        }

        DecoratorProvider decoratorProvider = context.getDecoratorProvider();

        boolean reloadDecorators = false;
        if (!config.getLocale().equals(
                I18n.getStore().getCurrentLocale())) {
            if (log.isInfoEnabled()) {
                log.info("re-init I18n with locale " + config.getLocale());
            }
            I18n.init(new DefaultI18nInitializer("episodesmanager-swing"), config.getLocale());
            reloadDecorators = true;
        }

        if (reloadDecorators) {
            if (log.isInfoEnabled()) {
                log.info("reload decorators");
            }
            decoratorProvider.reload();
        }

        JAXXInitialContext tx = new JAXXInitialContext();

        // init tray icon
        if (EpisodesTrayIcon.get()==null){
            log.info("init tray icon");
            EpisodesTrayIcon.init(context);
        }

        // show main ui
        EpisodesManagerMainUI ui = new EpisodesManagerMainUI(tx);
        ui.setUndecorated(config.isFullScreen());

        context.setEpisodesManagerMainUI(ui);

        //EpisodesManagerContext.MAIN_UI_ENTRY_DEF.setContextValue(rootContext, ui);

        //ErrorDialogUI.init(ui);

        // set fullscreen propery on main ui
        ui.getGraphicsConfiguration().getDevice().setFullScreenWindow(
                config.isFullScreen() ? ui : null);

        context.setContextValue(episodesListHandler);
        changeContent(context, episodesListHandler.initUI(context, this));

        return ui;
    }

    protected void changeContent(JAXXContext context, JPanel content){
        EpisodesManagerMainUI ui = getUI(context);
        ui.setContentPane(content);
        ui.setVisible(true);
    }

    protected void changeLanguage(Locale newLocale) {

        EpisodesManagerConfig config = EpisodesManagerContext.getConfig();

        // sauvegarde de la nouvelle locale
        config.setLocale(newLocale);
        // on recharge l'ui
        reloadUI();
    }

    /**
     * Ferme l'application.
     *
     * @param ui l'ui principale de l'application
     */
    public void close(EpisodesManagerMainUI ui) {
        log.info("Episodes Manager quitting...");
        boolean canContinue = ensureModification(ui);
        if (!canContinue) {
            return;
        }
        try {
            ui.dispose();
        } finally {
            System.exit(0);
        }
    }

    /**
     * Méthode pour changer de mode d'affichage.
     * <p/>
     * Si <code>fullscreen</code> est à <code>true</code> alors on passe en
     * mode console (c'est à dire en mode plein écran exclusif), sinon on
     * passe en mode fenetré normal.
     *
     * @param ui         l'ui principale de l'application
     * @param fullscreen le nouvel état requis.
     */
    protected void changeScreen(EpisodesManagerMainUI ui, final boolean fullscreen) {
        boolean canContinue = ensureModification(ui);
        if (!canContinue) {
            return;
        }

        EpisodesManagerConfig config = EpisodesManagerContext.getConfig();

        config.setFullScreen(fullscreen);

        reloadUI();
    }

    protected void showConfig() {
        EpisodesManagerContext context = EpisodesManagerContext.get();
        EpisodesManagerMainUI ui = getUI(context);
        EpisodesManagerConfig config = context.getContextValue(EpisodesManagerConfig.class);

        log.info("config : " + config);

        // instanciate config helper
        ConfigUIHelper helper = new ConfigUIHelper(config.getApplicationConfig());

        // build the config ui
        buildConfigUI(helper);

        helper.displayUI(ui, false);
    }

    protected void gotoSite() {

        EpisodesManagerConfig config = EpisodesManagerContext.getConfig();

/*        URL siteURL = config.getOptionAsURL("application.site.url");

        log.info("goto " + siteURL);
        
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(siteURL.toURI());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                ErrorDialogUI.showError(ex);
            }
        }*/
    }

    protected void showAbout(EpisodesManagerMainUI ui) {

        AboutPanel about = new AboutPanel() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buildTopPanel() {
                topPanel.setLayout(new BorderLayout());
                JLabel labelIcon;
                Icon logoIcon;
                logoIcon = Resource.getIcon("/icons/logo-OT_web.png");
                labelIcon = new JLabel(logoIcon);
                topPanel.add(labelIcon, BorderLayout.WEST);

                logoIcon = Resource.getIcon("/icons/logo_ird.png");
                labelIcon = new JLabel(logoIcon);
                topPanel.add(labelIcon, BorderLayout.EAST);
            }
        };

        about.setTitle(_("episodesmanager.title.about"));
        about.setAboutText(_("episodesmanager.about.message"));

        String bottomText = "Episodes Manager -v." +
                EpisodesManagerContext.getConfig().getApplicationVersion() +
                " -Copyright©2009 - 2011 Jean Couteau";

        about.setBottomText(bottomText);
        //about.setIconPath("/icons/logo-OT_web.png");
        about.setLicenseFile("META-INF/episodesmanager-swing-LICENSE.txt");
        about.setThirdpartyFile("META-INF/episodesmanager-swing-THIRD-PARTY.txt");
        about.init();
        about.showInDialog(ui, true);
    }

    /**
     * Permet de recharger l'ui principale.
     */
    protected void reloadUI() {

        EpisodesManagerContext context = EpisodesManagerContext.get();

        // scan main ui
        EpisodesManagerMainUI ui = getUI(context);

        if (ui != null) {

            ui.dispose();

            ui.setVisible(false);
        }

        ui = initUI();

        // show ui
        ui.setVisible(true);
    }

    /**
     * Test if there is some modification on screen,
     *
     * @param rootContext the context
     * @return <code>true</code> if no more modification is detected
     * @throws IllegalArgumentException if rootContext is null
     */
    protected boolean ensureModification(JAXXContext rootContext) throws IllegalArgumentException {
        if (rootContext == null) {
            throw new IllegalArgumentException("rootContext can not be null");
        }
        EpisodesManagerMainUI ui = getUI(rootContext);
        if (ui == null) {
            // no ui, so no modification
            return true;
        }
        // check ui is not modified
        return true;
    }

    EpisodesManagerMainUI getUI(JAXXContext context) {
        if (context instanceof EpisodesManagerMainUI) {
            return (EpisodesManagerMainUI) context;
        }

        return EpisodesManagerContext.MAIN_UI_ENTRY_DEF.getContextValue(context);
    }

    protected void search() {

        SearchTVRageUI ui = EpisodesManagerContext.SEARCH_UI_ENTRY_DEF.getContextValue(EpisodesManagerContext.get());

        if (ui == null) {

            ui=new SearchTVRageUI(EpisodesManagerContext.get());

            EpisodesManagerContext.SEARCH_UI_ENTRY_DEF.setContextValue(EpisodesManagerContext.get(), ui);
        }

        ui.setVisible(true);
    }

    public void searchResults(String search) {

        SearchResultHandler handler = new SearchResultHandler(search);
        handler.initUI(EpisodesManagerContext.get(), this);

        EpisodesManagerContext.get().setContextValue(handler);

        SearchTVRageResultsUI ui = new SearchTVRageResultsUI(EpisodesManagerContext.get());

        EpisodesManagerContext.SEARCHRESULTS_UI_ENTRY_DEF.setContextValue(EpisodesManagerContext.get(), ui);

        ui.setVisible(true);
    }

    protected void showAdminShows() {

        AdminShowsHandler handler = new AdminShowsHandler();
        handler.initUI(EpisodesManagerContext.get(), this);

        EpisodesManagerContext.get().setContextValue(handler);

        AdminShowsUI ui = new AdminShowsUI(EpisodesManagerContext.get());

        EpisodesManagerContext.ADMINSHOWS_UI_ENTRY_DEF.setContextValue(EpisodesManagerContext.get(), ui);

        ui.setVisible(true);
    }

    protected void showTimeSpent() {

        log.debug("Show time spent");

        TimeSpentHandler handler = new TimeSpentHandler();

        EpisodesManagerContext.get().setContextValue(handler);

        TimeSpentUI ui = handler.initUI(EpisodesManagerContext.get());

        handler.updateUI();

        ui.setVisible(true);
    }

    protected void update() {
        updateShows(0,600000);
    }

    protected void exportXML(Component ui){

        XmlService service = EpisodesManagerHelper.newService(XmlService.class);

        fc.setFileFilter(new XmlFilter());

        int returnVal = fc.showDialog(ui,"Export");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if ("xml".equals(getExtension(file))){
                log.debug("Export to : " + file.getName());
                service.exportToXML(file);
            }
        } else {
            log.debug("Open command cancelled by user.");
        }
    }

    protected void exportCSV(Component ui){

        CsvService service = EpisodesManagerHelper.newService(CsvService.class);

        fc.setFileFilter(new CsvFilter());

        int returnVal = fc.showDialog(ui, "Export");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if ("csv".equals(getExtension(file))){
                log.debug("Export to : " + file.getName());
                service.exportToCSV(file);
            }
        } else {
            log.debug("Open command cancelled by user.");
        }
    }

    protected void importXML(Component ui){

        XmlService service = EpisodesManagerHelper.newService(XmlService.class);

        fc.setFileFilter(new XmlFilter());

        int returnVal = fc.showDialog(ui, "Import");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if ("xml".equals(getExtension(file))){
                log.debug("Import from : " + file.getName());
                service.importFromXML(file);
                updateMainUI();
            }
        } else {
            log.debug("Open command cancelled by user.");
        }
    }

    protected void importCSV(Component ui){

        CsvService service = EpisodesManagerHelper.newService(CsvService.class);

        fc.setFileFilter(new CsvFilter());

        int returnVal = fc.showDialog(ui, "Import");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if ("csv".equals(getExtension(file))){
                log.debug("Import from : " + file.getName());
                service.importFromCSV(file);
                updateMainUI();
            }
        } else {
            log.debug("Open command cancelled by user.");
        }
    }

    private String getExtension(File f){
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    private void updateMainUI(){

        EpisodesListUI episodesListUI = EpisodesManagerContext.EPISODES_LIST_UI_ENTRY_DEF.getContextValue(EpisodesManagerContext.get());

        if (episodesListUI != null){
            EpisodesListHandler handler = episodesListUI.getHandler();

            EpisodesListTableModel tableModel = handler.getEpisodesListTableModel();

            tableModel.update();
        }
    }

    public void createSystray(){
        EpisodesTrayIcon systray = EpisodesTrayIcon.get();
        systray.create();
    }

    /**
     * Regularly retrieves the information from all the xml streams
     * and create new forms
     *
     * @param delay  the delay before the first retrieving
     * @param period interval between two retrievings
     */
    public void updateShows(long delay, long period) {
        if (timer == null) {
            timer = new java.util.Timer();
        }
        if (updateTask != null) {
            updateTask.cancel();
            timer.purge();
        }

        updateTask = new TimerTask() {
            @Override
            public void run() {
                if (log.isInfoEnabled()) {
                    log.info("Update shows task");
                }

                ShowsService service = EpisodesManagerHelper.newService(ShowsService.class);
				TheTvDbService webService = EpisodesManagerHelper.newService(TheTvDbService.class);

				webService.updateShows();
				if (log.isDebugEnabled()){
                        log.debug("Update UI");
                    }
                    updateMainUI();
            }
        };

        timer.scheduleAtFixedRate(updateTask, delay, period);
    }

    public void buildConfigUI(ConfigUIHelper helper) {

        Runnable reloadUICallback = new Runnable() {

            @Override
            public void run() {
                if (log.isInfoEnabled()) {
                    log.info("Reload UI");
                }
                reloadUI();
            }
        };

        Runnable reloadApplicationCallback = new Runnable() {

            @Override
            public void run() {

                if (log.isInfoEnabled()) {
                    log.info("Reload appplication");
                }

                EpisodesManagerRunner.main(null);

                reloadUI();
                
            }
        };

        helper.registerCallBack("ui",
                n_("episodesmanager.action.reload.ui"),
                SwingUtil.createActionIcon("about"),
                reloadUICallback);

        helper.registerCallBack("application",
                n_("episodesmanager.action.reload.application"),
                SwingUtil.createActionIcon("about"),
                reloadApplicationCallback);

        // categorie repertoires

        helper.addCategory(n_("episodesmanager.config.category.directories"),
                n_("episodesmanager.config.category.directories.description"));

        helper.addOption(EpisodesManagerConfigOption.CONFIG_FILE);

        // others
        helper.addCategory(n_("episodesmanager.config.category.other"),
                n_("episodesmanager.config.category.other.description"));

        helper.addOption(EpisodesManagerConfigOption.FULLSCREEN);
        helper.setOptionPropertyName(EpisodesManagerConfigOption.FULLSCREEN.getKey());
        helper.setOptionCallBack("ui");

        helper.addOption(EpisodesManagerConfigOption.LOCALE);
        helper.setOptionPropertyName(EpisodesManagerConfigOption.LOCALE.getKey());
        helper.setOptionCallBack("ui");

        helper.buildUI(new JAXXInitialContext(),
                "episodesmanager.config.category.other");

    }
}
