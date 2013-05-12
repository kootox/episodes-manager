package org.kootox.episodesmanager;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.kootox.episodesmanager.entities.EpisodesManagerDAOHelper;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaContextFactory;
import org.nuiton.topia.TopiaNotFoundException;
import org.nuiton.topia.TopiaRuntimeException;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class EpisodesManagerTopiaRootContextSupplierFactory {
    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(EpisodesManagerTopiaRootContextSupplierFactory.class);

    protected static class EpisodesManagerTopiaRootContextSupplier implements Supplier<TopiaContext> {

        protected TopiaContext rootContext;

        public EpisodesManagerTopiaRootContextSupplier(TopiaContext rootContext) {
            this.rootContext = rootContext;
        }

        @Override
        public TopiaContext get() {
            return rootContext;
        }
    }

    public Supplier<TopiaContext> newEmbeddedDatabase(File dir) {

        File databaseFile = new File(dir, "h2-db");

        String databaseAbsolutePath = databaseFile.getAbsolutePath();

        // prepare call to topia-context factory
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = getClass().getResourceAsStream("/topia-h2.properties");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(input);
        }
        properties.setProperty(Environment.URL,
                "jdbc:h2:file:" + databaseAbsolutePath);

        // add entities to the context
        properties.setProperty(
                TopiaContextFactory.CONFIG_PERSISTENCE_CLASSES,
                EpisodesManagerDAOHelper.getImplementationClassesAsString());

        TopiaContext rootContext;
        try {
            rootContext = TopiaContextFactory.getContext(properties);
        } catch (TopiaNotFoundException e) {
            throw new TopiaRuntimeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("will output database in " + databaseAbsolutePath);
        }

        return new EpisodesManagerTopiaRootContextSupplier(rootContext);
    }

    public Supplier<TopiaContext> newDatabaseFromConfig(EpisodesManagerConfig config) {

        Properties properties = config.getProperties();

        if (log.isDebugEnabled()) {
            log.debug("Database settings are :");
            Set<String> keysToDisplay = Sets.newHashSet(
                    "hibernate.dialect",
                    "hibernate.connection.driver_class",
                    "hibernate.connection.url",
                    "hibernate.connection.username");
            for (String key : keysToDisplay) {
                log.debug(String.format("%s=%s", key, properties.getProperty(key)));
            }
        }

        // add entities to the context
        String classesKey = TopiaContextFactory.CONFIG_PERSISTENCE_CLASSES;
        String classesValue = EpisodesManagerDAOHelper.getImplementationClassesAsString();
        properties.setProperty(classesKey, classesValue);

        TopiaContext rootContext;
        try {
            rootContext = TopiaContextFactory.getContext(properties);
        } catch (TopiaNotFoundException e) {
            throw new TopiaRuntimeException(e);
        }

        return new EpisodesManagerTopiaRootContextSupplier(rootContext);
    }
}
