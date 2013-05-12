package org.kootox.episodesmanager.android.utility;

/**
 * User: couteau
 * Date: 15 mai 2010
 */
public class EpisodesManagerException extends Exception{

    private static final long serialVersionUID = 1L;

    public EpisodesManagerException() {
        super();
    }

    public EpisodesManagerException(String message) {
        super(message);
    }

    public EpisodesManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EpisodesManagerException(Throwable cause) {
        super(cause);
    }

}
