package org.kootox.episodesmanager;

import java.io.File;
import java.util.Locale;
import org.nuiton.util.ApplicationConfig;

import static org.nuiton.i18n.I18n._;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public enum EpisodesManagerConfigOption implements ApplicationConfig.OptionDef {

    CONFIG_FILE(
            "episodesManager.config.file",
            _("episodesManager.config.configFileName.description"),
            "episodesManager.properties", String.class),

    APPLICATION_VERSION(
            "application.version",
            _("refcomp.config.application.version.description"),
            null, String.class),

    DATA_DIR(
            "episodesManager.data.dir",
            _("episodesManager.config.data.dir.description"),
            "${user.home}" + File.separator + ".episodesManager", String.class),

    LOCALE(
            "episodesManager.locale",
            _("episodesManager.config.ui.locale.description"),
            Locale.FRANCE.toString(), Locale.class),

    API_LOCALE(
            "episodesManager.api_locale",
            _("episodesManager.config.ui.api_locale.description"),
            Locale.FRANCE.toString(), Locale.class),

    THE_TV_DB_API_KEY(
            "episodesManager.theTvDbApiKey",
            _("episodesManager.config.ui.theTvDbApiKey.description"),
            "605B6925B7B887D7", String.class),

    TEMP_DIRECTORY(
            "episodesManager.tempDirectory",
            _("episodesManager.config.ui.tempDirectory.description"),
            "/tmp/episodes-manager/", File.class),

    ARTWORK_DIRECTORY(
            "episodesManager.artworkDirectory",
            ("episodesManager.config.ui.artworkDirectory.description"),
            "${user.home}" + File.separator + ".episodesManager" + File.separator + "artwork",
            File.class),

    THE_TV_DB_LAST_UPDATED(
            "episodesManager.theTvDbLastUpdated",
            ("episodesManager.config.ui.theTvDbLastUpdated.description"),
            "1", Long.class),

    FULLSCREEN(
            "episodesManager.fullscreen",
            _("episodesManager.config.ui.fullscreen.description"),
            "false", Boolean.class),

    WEB_DATABASE(
            "episodesManager.webdatabase",
            _("episodesManager.config.ui.webdatabase.description"),
            "tvrage", String.class);

    public static final String DATA_DEFAULT_DIR = "${user.home}" + File.separator + ".episodesManager" +
            File.separator + "episodesManager-${application.version}";

    protected final String key;
    protected final String description;
    protected final Class<?> type;
    protected String defaultValue;

    private EpisodesManagerConfigOption(String key, String description,
                                   String defaultValue, Class<?> type) {
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void setTransient(boolean isTransient) {
        // Nothing to do
    }

    @Override
    public void setFinal(boolean isFinal) {
        // Nothing to do
    }

}
