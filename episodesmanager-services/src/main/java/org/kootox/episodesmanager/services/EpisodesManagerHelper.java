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
package org.kootox.episodesmanager.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.nuiton.util.ApplicationConfig;

/**
 * @author jcouteau
 */
public class EpisodesManagerHelper {

    private static final Log log = LogFactory.getLog(EpisodesManagerHelper.class);

    protected static ServiceContext serviceContext;

    protected static EpisodesManagerConfig config;

    /**
     * Provides a way to get a service.
     * <p/>
     * UIs may <strong>not</strong> call it directly but use
     * {@link #newService(Class)} instead.
     */
    protected static ServiceFactory serviceFactory = new ServiceFactory();

    /** Private default constructor to avoid instantiation **/
    private EpisodesManagerHelper(){}

    public static <E extends EpisodesManagerService> E newService(Class<E> serviceClass) {
        E service = serviceFactory.newService(serviceClass, getServiceContext());
        return service;
    }

    /**
     * Fabrique pour récupérer le ServiceContext tel qu'il devrait être fourni
     * à la fabrication d'un service.
     */
    protected static ServiceContext getServiceContext() {
        if (serviceContext == null) {
            serviceContext = new ServiceContextImpl(getConfig(), serviceFactory);
        }
        return serviceContext;
    }
    
    protected static EpisodesManagerConfig getConfig(){
        if (config == null) {
            config = new EpisodesManagerConfig();
        }
        return config;
    }
}
