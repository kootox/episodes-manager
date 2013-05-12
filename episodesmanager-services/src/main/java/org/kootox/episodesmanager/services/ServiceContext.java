/*
 * #%L
 * Extranet NF-Logement :: Business
 * 
 * $Id: ServiceContext.java 424 2011-09-20 12:04:44Z athimel $
 * $HeadURL: http://svn.forge.codelutin.com/svn/extranet-nf-logement/trunk/services/src/main/java/fr/cerqual/nflogement/extranet/services/ServiceContext.java $
 * %%
 * Copyright (C) 2011 Cerqual
 * %%
 * Tous droits réservés à Cerqual
 * #L%
 */
package org.kootox.episodesmanager.services;

import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.nuiton.topia.TopiaContext;

/** This contract represents objects you must provide when asking for a service.
 * Objects provided may be injected in services returned by
 * {@link ServiceFactory#newService(Class, ServiceContext)}
 */
public interface ServiceContext {

    TopiaContext getTransaction();

    <E extends EpisodesManagerService> E newService(Class<E> clazz);

    EpisodesManagerConfig getEpisodesManagerConfig();

    void closeTransaction(TopiaContext transaction);
}
