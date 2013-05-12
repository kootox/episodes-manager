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

import com.google.common.base.Supplier;
import jaxx.runtime.SwingUtil;
import jaxx.runtime.swing.application.ApplicationRunner;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.services.ServiceContext;
import org.kootox.episodesmanager.services.ServiceContextImpl;
import org.kootox.episodesmanager.services.ServiceFactory;
import org.kootox.episodesmanager.ui.EpisodesManagerMainUI;
import org.kootox.episodesmanager.ui.EpisodesManagerMainUIHandler;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.util.StringUtil;
import org.nuiton.util.converter.ConverterUtil;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import org.nuiton.util.converter.LocaleConverter;

import static org.nuiton.i18n.I18n._;

/**
 * User: couteau
 * Date: 29 juin 2010
 */
public class EpisodesManagerRunner extends ApplicationRunner {

    /**
     * Logger
     */
    private static Log log = LogFactory.getLog(EpisodesManagerRunner.class);

    public EpisodesManagerRunner(String[] args) {
        super(args);
    }

    public static void main(String[] args) {

        log.info("Episodes Manager launched at " + new Date() + " args: " + Arrays.toString(args));

        new EpisodesManagerRunner(args).launch();
    }

    @Override
    protected void initOnce() {

        // on veut avoir les traductions dès le début
        // on charge dans un premier temps les traductions fournies
        // par l'application
        I18n.init(new DefaultI18nInitializer("episodesmanager-swing-i18n"), Locale.getDefault());

        // initialisation des converteurs
        Converter converter = ConverterUtil.getConverter(Date.class);
        if (converter != null) {
            ConvertUtils.deregister(Date.class);

            DateConverter dateConverter = new DateConverter();
            dateConverter.setUseLocaleFormat(true);
            ConvertUtils.register(dateConverter, Date.class);
        }
        ConvertUtils.register(new LocaleConverter(), Locale.class);

    }

    @Override
    protected void onInit() throws Exception {

        log.info(_("episodesmanager.runner.init", new Date(), Arrays.toString(args)));

        long t0 = System.nanoTime();

        // 1 - preparation de la configuration

        EpisodesManagerConfig config = initConfig();

        log.info(_("episodesmanager.runner.config.loaded", config.getApplicationVersion()));

        // 3 - preparation i18n

        initI18n(config);

        log.info(_("episodesmanager.runner.i18n.loaded", config.getLocale().getDisplayLanguage()));

        // 4 - preparation de la configuration des ui

        initUIConfiguration();

        // 5 - preparation du context applicatif

        EpisodesManagerContext context = initContext(config);

        log.info("Will use context :" + context);

        String time = StringUtil.convertTime(t0, System.nanoTime());

        log.info(_("episodesmanager.runner.context.loaded", time));
    }

    @Override
    protected void onStart() throws Exception {

        log.info(_("episodesmanager.runner.start", new Date(), Arrays.toString(args)));

        EpisodesManagerContext context = EpisodesManagerContext.get();
        
        startUI(context);

        log.info(_("episodesmanager.runner.ui.loaded"));

    }

    @Override
    protected void onClose(boolean reload) throws Exception {
        if (EpisodesManagerContext.isInit()) {

            if (log.isDebugEnabled()) {
                log.debug("Will close context...");
            }
            EpisodesManagerContext.get().close();
        }
    }

    @Override
    protected void onShutdown() throws Exception {
        log.info("EpisodesManager shutdown at " + new Date());

        // on ferme le service de traduction uniquement si on quitte
        // definitivement l'application
        I18n.close();

        Runtime.getRuntime().halt(0);
    }

    @Override
    protected void onShutdown(Exception ex) {
        log.error("error while closing " + ex.getMessage(), ex);
        Runtime.getRuntime().halt(1);
    }

    @Override
    protected void onError(Exception e) {
        log.error(e.getMessage(), e);
    }

    protected EpisodesManagerConfig initConfig() throws Exception {

        EpisodesManagerConfig config = new EpisodesManagerConfig();

        // init config arguments
        config.parse(args);

        return config;
    }

    protected void initI18n(EpisodesManagerConfig config) {

        long t00 = System.nanoTime();

        // init i18n
        I18n.init(new DefaultI18nInitializer("episodesmanager-swing-i18n"), config.getLocale());

        log.info("language : " + config.getLocale());

        if (log.isDebugEnabled()) {
            log.debug("i18n loading time : " + (StringUtil.convertTime(t00, System.nanoTime())));
        }
    }

    protected void initUIConfiguration() throws IOException {

        // prepare ui look&feel and load ui properties
        try {
            SwingUtil.initNimbusLoookAndFeel();
        } catch (Exception e) {
            // could not find nimbus look-and-feel
            log.warn(_("episodesmanager.warning.nimbus.landf"));
        } catch (Throwable e) {
            log.warn(_("episodesmanager.warning.no.ui"));
        }

    }

    /**
     * Permet l'initialisation du contexte applicatif et positionne l'instance
     * partagée.
     * <p/>
     * Note : Cette méthode ne peut être appelée qu'une seule fois.
     *
     * @param config la configuration de l'application
     * @return le context applicatif
     * @throws IllegalStateException si un contexte applicatif a déja été positionné.
     */
    public EpisodesManagerContext initContext(EpisodesManagerConfig config) throws Exception {
        if (EpisodesManagerContext.isInit()) {
            throw new IllegalStateException(
                    "there is an already application context registred.");
        }

        EpisodesManagerContext instance = EpisodesManagerContext.init();

        EpisodesManagerTopiaRootContextSupplierFactory factory =
                new EpisodesManagerTopiaRootContextSupplierFactory();
        Supplier rootContextSupplier = factory.newDatabaseFromConfig(config);

        // add config
        EpisodesManagerContext.CONFIG_ENTRY_DEF.setContextValue(instance, config);
        EpisodesManagerContext.ROOTCONTEXT_SUPPLIER_ENTRY_DEF.setContextValue(instance, rootContextSupplier);

        ServiceFactory serviceFactory = new ServiceFactory();
        ServiceContext serviceContext = new ServiceContextImpl(config, serviceFactory);

        EpisodesManagerContext.SERVICECONTEXT_ENTRY_DEF.setContextValue(instance, serviceContext);

        return instance;
    }

    protected void startUI(EpisodesManagerContext context) {

        EpisodesManagerMainUIHandler uiHandler =
                context.getContextValue(EpisodesManagerMainUIHandler.class);

        final EpisodesManagerMainUI ui = uiHandler.initUI();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ui.setVisible(true);
            }
        });
    }

}
