package org.kootox.episodesmanager.exceptions;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class EpisodesManagerTechnicalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EpisodesManagerTechnicalException() {
        super();
    }

    public EpisodesManagerTechnicalException(String message) {
        super(message);
    }

    public EpisodesManagerTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public EpisodesManagerTechnicalException(Throwable cause) {
        super(cause);
    }
}
