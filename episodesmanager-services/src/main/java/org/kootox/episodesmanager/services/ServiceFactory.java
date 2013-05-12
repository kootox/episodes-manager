package org.kootox.episodesmanager.services;

import java.lang.reflect.InvocationTargetException;
import org.kootox.episodesmanager.exceptions.EpisodesManagerTechnicalException;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class ServiceFactory {

    public <E extends EpisodesManagerService> E newService(Class<E> clazz, ServiceContext serviceContext) {
        // instantiate service using empty constructor
        E service;
        try {
            service = clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new EpisodesManagerTechnicalException(e);
        } catch (IllegalAccessException e) {
            throw new EpisodesManagerTechnicalException(e);
        } catch (InvocationTargetException e) {
            throw new EpisodesManagerTechnicalException(e);
        } catch (NoSuchMethodException e) {
            throw new EpisodesManagerTechnicalException(e);
        }

        service.setServiceContext(serviceContext);

        return service;
    }
}
