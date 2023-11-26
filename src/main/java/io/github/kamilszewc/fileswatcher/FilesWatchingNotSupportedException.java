package io.github.kamilszewc.fileswatcher;

/**
 * Exception indicating that files watching is not supported
 */
public class FilesWatchingNotSupportedException extends Exception {

    /**
     * FilesWatchingNotSupportedException constructor
     */
    public FilesWatchingNotSupportedException() {
        super();
    }

    /**
     * FilesWatchingNotSupportedException constructor
     * @param ex Parent exception
     */
    public FilesWatchingNotSupportedException(Exception ex) {
        super(ex);
    }

    /**
     * FilesWatchingNotSupportedException constructor
     * @param message Message
     * @param ex Parent exception
     */
    public FilesWatchingNotSupportedException(String message, Exception ex) {
        super(message, ex);
    }
}
