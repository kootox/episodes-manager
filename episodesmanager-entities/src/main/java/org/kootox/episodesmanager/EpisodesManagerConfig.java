/*
 * #%L
 * episodesmanager-services
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

import java.util.Properties;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.kootox.episodesmanager.exceptions.EpisodesManagerTechnicalException;
import org.nuiton.util.ApplicationConfig;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import org.nuiton.util.ArgumentsParserException;

import static org.kootox.episodesmanager.EpisodesManagerConfigOption.*;

/**
 * User: couteau
 * Date: 22 juil. 2010
 */
public class EpisodesManagerConfig {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(EpisodesManagerConfig.class);

    protected ApplicationConfig applicationConfig;

    public EpisodesManagerConfig() {
        applicationConfig = new ApplicationConfig("episodes-manager.properties");
        applicationConfig.loadDefaultOptions(EpisodesManagerConfigOption.class);
        try {
            applicationConfig.parse();
        } catch (ArgumentsParserException e) {
            throw new EpisodesManagerTechnicalException(e);
        }
        if (log.isDebugEnabled()) {
            log.debug("parsed options in config file " + applicationConfig.getOptions());
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public Properties getProperties() {
        Properties result = applicationConfig.getFlatOptions();
        return result;
    }

    public void parse(String... args){
        try {
            applicationConfig.parse(args);
        } catch (ArgumentsParserException eee) {
            log.warn("Could not parse arguments");
        }
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    /**
     * Get current application version as string.
     *
     * @return version
     */
    public String getApplicationVersion() {
        return applicationConfig.getOption(APPLICATION_VERSION.key);
    }

    public String getDataDir() {
        String option = applicationConfig.getOption(DATA_DIR.key);
        return option;
    }

    public File getDataDirAsFile() {
        File option = applicationConfig.getOptionAsFile(DATA_DIR.key);
        return option;
    }

    public Locale getLocale() {
        Locale result = applicationConfig.getOption(Locale.class, LOCALE.key);
        return result;
    }

    public void setLocale(Locale locale) {
        applicationConfig.setOption(LOCALE.key, locale.toString());
    }

    public Locale getApiLocale() {
        Locale result = applicationConfig.getOption(Locale.class, API_LOCALE.key);
        return result;
    }

    public void setApiLocale(Locale locale) {
        applicationConfig.setOption(API_LOCALE.key, locale.toString());
    }

    public String getTheTvDbApiKey() {
        String result = applicationConfig.getOption(String.class, THE_TV_DB_API_KEY.key);
        return result;
    }

    public Long getTheTvDbLastUpdated() {
        Long result = applicationConfig.getOption(Long.class, THE_TV_DB_LAST_UPDATED.key);
        return result;
    }

	public void setTheTvDbLastUpdated(Long timestamp) {
		applicationConfig.setOption(THE_TV_DB_LAST_UPDATED.key, timestamp.toString());
	}

    public File getTempDirectory() {
        File result = applicationConfig.getOptionAsFile(TEMP_DIRECTORY.key);
        if (!result.exists()) {
            try {
                boolean created = result.mkdirs();

                if (log.isDebugEnabled() && created) {
                    log.debug("Temporary file successfully created : " + result);
                }

            } catch (Exception eee) {
                log.error("Could not create temp file", eee);
            }
        }
        return result;
    }

    public File getArtworkDirectory() {
        File result = applicationConfig.getOptionAsFile(ARTWORK_DIRECTORY.key);
        if (!result.exists()) {
            try {
                boolean created = result.mkdirs();

                if (log.isDebugEnabled() && created) {
                    log.debug("Artwork directory successfully created : " + result);
                }

            } catch (Exception eee) {
                log.error("Could not create artwork directory", eee);
            }
        }
        return result;
    }

    public boolean isFullScreen() {
        return applicationConfig.getOptionAsBoolean(FULLSCREEN.key);
    }

    public void setFullScreen(Boolean fullScreen){
        applicationConfig.setOption(FULLSCREEN.key, fullScreen.toString());
    }

    public String getWebDatabase() {
        return applicationConfig.getOption(WEB_DATABASE.key);
    }

	public void setWebDatabase(String webDatabase) {
		applicationConfig.setOption(WEB_DATABASE.key, webDatabase);
	}

	public void save() {
		applicationConfig.saveForUser();
	}
}
