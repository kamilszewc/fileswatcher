package io.github.kamilszewc.fileswatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ChangedFileEvent class used to record information about last event of modifying/creating file.
 */
public class ChangedFileEvent {
    private final Path path;
    private final WatchEvent.Kind eventKind;
    private final LocalDateTime timestamp;

    /**
     * Constructor
     * @param path Path to file
     * @param eventKind Kind of event
     */
    public ChangedFileEvent(Path path, WatchEvent.Kind eventKind) {
        this.path = path;
        this.eventKind = eventKind;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns path to file
     * @return path to file
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Returns kind of event
     * @return kind of event
     */
    public WatchEvent.Kind getEventKind() {
        return this.eventKind;
    }

    /**
     * Returns timestamp of event
     * @return timestamp of event
     */
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "ChangedFileEvent(path=" + path + ", eventKind=" + eventKind + ", timestamp=" + timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChangedFileEvent other = (ChangedFileEvent) obj;
        if (other.path.equals(this.path)) {
            return true;
        } else {
            return false;
        }
    }

}
