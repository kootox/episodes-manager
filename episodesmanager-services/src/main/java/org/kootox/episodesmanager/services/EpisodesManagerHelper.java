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

import static org.nuiton.i18n.I18n._;

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

    public static String convertIntoDays(long minutes) {
        long finalMinutes;
        long finalHours;
        long finalDays;
        long finalWeeks;
        long finalMonths;
        long finalYears;

        //calculate finalMinutes;
        finalHours = minutes/60;
        finalMinutes = minutes - finalHours*60;

        //calculate finalHours;
        finalDays = finalHours/24;
        finalHours = finalHours - finalDays*24;

        //calculate finalDays;
        finalWeeks = finalDays/7;
        finalDays = finalDays - finalWeeks*7;

        //calculate finalWeeks;
        //Use 13/3 value, simplification of 52/12,
        //normalized number of weeks per month.
        finalMonths = finalWeeks * 3/13;
        finalWeeks = finalWeeks - finalMonths * 13/3;

        //calculate finalMonths and finalYears;
        finalYears = finalMonths/12;
        finalMonths = finalMonths - finalYears*12;

        StringBuilder builder = new StringBuilder();

        if (finalYears!=0){
            builder.append(finalYears);
            if (finalYears==1){
                builder.append(_("episodesmanager.timespent.year"));
            } else {
                builder.append(_("episodesmanager.timespent.years"));
            }
            if ((finalMonths!=0)||(finalWeeks!=0)||(finalDays!=0)||(finalHours!=0)||(finalMinutes!=0)){
                builder.append(", ");
            }
        }

        if (finalMonths!=0){
            builder.append(finalMonths);
            if (finalMonths == 1) {
                builder.append(_("episodesmanager.timespent.month"));
            } else {
                builder.append(_("episodesmanager.timespent.months"));
            }
            if ((finalWeeks != 0) || (finalDays != 0) || (finalHours != 0) || (finalMinutes != 0)) {
                builder.append(", ");
            }
        }

        if (finalWeeks!=0){
            builder.append(finalWeeks);
            if (finalWeeks == 1) {
                builder.append(_("episodesmanager.timespent.week"));
            } else {
                builder.append(_("episodesmanager.timespent.weeks"));
            }
            if ((finalDays != 0) || (finalHours != 0) || (finalMinutes != 0)) {
                builder.append(", ");
            }
        }

        if (finalDays!=0){
            builder.append(finalDays);
            if (finalDays == 1) {
                builder.append(_("episodesmanager.timespent.day"));
            } else {
                builder.append(_("episodesmanager.timespent.days"));
            }
            if ((finalHours != 0) || (finalMinutes != 0)) {
                builder.append(", ");
            }
        }

        if (finalHours!=0){
            builder.append(finalHours);
            if (finalHours == 1) {
                builder.append(_("episodesmanager.timespent.hour"));
            } else {
                builder.append(_("episodesmanager.timespent.hours"));
            }
            if (finalMinutes != 0) {
                builder.append(", ");
            }
        }

        if (finalMinutes!=0){
            builder.append(finalMinutes);
            if (finalMinutes == 1) {
                builder.append(_("episodesmanager.timespent.minute"));
            } else {
                builder.append(_("episodesmanager.timespent.minutes"));
            }
        }

        return builder.toString();
    }
}
