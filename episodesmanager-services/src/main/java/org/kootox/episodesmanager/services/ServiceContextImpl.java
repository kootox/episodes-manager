package org.kootox.episodesmanager.services;

import com.google.common.base.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.kootox.episodesmanager.EpisodesManagerTopiaRootContextSupplierFactory;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;
import org.nuiton.topia.TopiaRuntimeException;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class ServiceContextImpl implements ServiceContext{
    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(ServiceContextImpl.class);

    protected ServiceFactory serviceFactory;

    protected EpisodesManagerConfig config;

    protected TopiaContext rootContext;

    protected Supplier<TopiaContext> rootContextSupplier;

    public ServiceContextImpl(EpisodesManagerConfig config, ServiceFactory serviceFactory) {
        this.config = config;
        this.serviceFactory = serviceFactory;

        EpisodesManagerTopiaRootContextSupplierFactory factory =
                new EpisodesManagerTopiaRootContextSupplierFactory();
        rootContextSupplier = factory.newDatabaseFromConfig(this.config);
        rootContext = rootContextSupplier.get();
    }

    @Override
    public TopiaContext getTransaction() {
        TopiaContext transaction;

        try {
            transaction = rootContext.beginTransaction();
        } catch (TopiaException e) {
            throw new TopiaRuntimeException(e);
        }

        return transaction;
    }

    @Override
    public <E extends EpisodesManagerService> E newService(Class<E> clazz) {
        return serviceFactory.newService(clazz, this);
    }

    @Override
    public EpisodesManagerConfig getEpisodesManagerConfig() {
        return config;
    }

    /**
     * Close the transaction. Log the error that might happen
     *
     * @param transaction the context to close
     */
    public void closeTransaction(TopiaContext transaction) {
        try {
            if (transaction != null) {
                transaction.closeContext();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot close topia context, not opened");
                }
            }
        } catch (TopiaException te) {
            log.fatal("Could not close context", te);
        }
    }
}
