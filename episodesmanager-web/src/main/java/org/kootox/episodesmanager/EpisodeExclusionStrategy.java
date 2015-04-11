package org.kootox.episodesmanager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.kootox.episodesmanager.entities.EpisodeAbstract;
import org.kootox.episodesmanager.entities.SeasonAbstract;
import org.kootox.episodesmanager.entities.ShowAbstract;

public class EpisodeExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == SeasonAbstract.class && f.getName().equals("episode"))||
                (f.getDeclaringClass() == ShowAbstract.class && f.getName().equals("season"))||
                (f.getDeclaringClass() == EpisodeAbstract.class && f.getName().equals("season"));
    }
}