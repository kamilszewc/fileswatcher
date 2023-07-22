package com.computinglaboratory.fileswatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * FilesWatcher class used to watch for changes in files inside specific directories
 */
public class FilesWatcher {
    private static final Logger log = LogManager.getLogger(FilesWatcher.class);

    private final WatchService watchService;
    private final Set<ChangedFileEvent> changedFileEvents = new HashSet<>();

    /**
     * Constructor
     * @param directory Directory to watch
     * @throws FilesWatchingNotSupportedException Raised when the operating system does not support file watching by JVM or there are some problems with registering file for watching
     */
    public FilesWatcher(Path directory) throws FilesWatchingNotSupportedException {
        this(new ArrayList<Path>() {{ add(directory); }});
    }

    /**
     * Constructor
     * @param directories Collection of directories to watch
     * @throws FilesWatchingNotSupportedException Raised when the operating system does not support file watching by JVM or there are some problems with registering file for watching
     */
    public FilesWatcher(Collection<Path> directories) throws FilesWatchingNotSupportedException {

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (Exception ex) {
            throw new FilesWatchingNotSupportedException("Can not create JVM Watch Service", ex);
        }

        for (Path directory : directories) {
            try {
                directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
            } catch (IOException ex) {
                throw new FilesWatchingNotSupportedException("Can not register directory " + directory + " for JVM Watch Service", ex);
            }
        }
    }

    /**
     * Start watching for file changes
     */
    public void watch() {
        for (;;) {
            // Wait for key to be signaled
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event: watchKey.pollEvents()) {
                WatchEvent.Kind<?> eventKind = event.kind();

                if (eventKind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;
                Path path = e.context();
                Path dir = (Path) watchKey.watchable();
                Path fullPath = dir.resolve(path);

                if (!Files.isDirectory(fullPath)) {
                    ChangedFileEvent changedFileEvent = new ChangedFileEvent(fullPath, eventKind);
                    changedFileEvents.add(changedFileEvent);
                    log.trace("Adding file event: " + changedFileEvent);
                }
            }

            boolean valid = watchKey.reset();
            if (!valid) {
                break;
            }
        }
    }

    /**
     * Returns set of recorded ChangedFileEvents
     * @return Set of recorded ChangedFileEvents
     */
    public Set<ChangedFileEvent> getChangedFileEvents() {
        return this.changedFileEvents;
    }

    /**
     * Returns set of paths to files with recorded changes
     * @return set of paths to files with recorded changes
     */
    public Set<Path> getChangedFilePaths() {
        return this.changedFileEvents.stream()
                .map(ChangedFileEvent::getPath)
                .collect(Collectors.toSet());
    }


    /**
     * Remove recoded ChangedFileEvents of specific paths
     * @param paths Collections of Paths to files
     */
    public void removeChangedFileEvents(Collection<Path> paths) {
        for (Path path : paths) {
            Optional<ChangedFileEvent> event = changedFileEvents.stream()
                    .filter(changedFileEvent -> changedFileEvent.getPath().equals(path))
                    .findFirst();
            event.ifPresent(changedFileEvents::remove);
        }
    }

    /**
     * Remove all recoded ChangedFileEvents
     */
    public void removeAllChangedFileEvents() {
        changedFileEvents.clear();
    }

}
