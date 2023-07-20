package com.computinglaboratory.fileswatcher

import spock.lang.Specification
import java.nio.file.*;

class FilesWatcherTest extends Specification {
    final Path file_1 = Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder\\file_1.txt")
    final Path file_2 = Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder\\file_2.txt")
    final Path file_3 = Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder\\file_3.txt")
    final Path file_4_sub = Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder\\subfolder\\file_4.txt")

    def "FilesWatcher detect files in single directory with no subdirectories registered"() {
        given:
        def filesWatcher = new FilesWatcher(Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder"));
        new Thread(() -> {
            filesWatcher.watch();
        }).start();

        when:
        createRandomFile(file_1)
        createRandomFile(file_2)
        createRandomFile(file_3)
        createRandomFile(file_4_sub)

        then:
        sleep(100)
        def changedFilePaths = filesWatcher.getChangedFilePaths()
        changedFilePaths.contains(file_1)
        changedFilePaths.contains(file_2)
        changedFilePaths.contains(file_3)
        !changedFilePaths.contains(file_4_sub)

        Files.delete(file_1)
        Files.delete(file_2)
        Files.delete(file_3)
        Files.delete(file_4_sub)
    }

    def "FilesWatcher detect files in single directory with subdirectories if registered"() {
        given:
        def filesWatcher = new FilesWatcher([Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder"),
                                             Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder\\subfolder")]);
        new Thread(() -> {
            filesWatcher.watch();
        }).start();

        when:
        createRandomFile(file_1)
        createRandomFile(file_2)
        createRandomFile(file_3)
        createRandomFile(file_4_sub)

        then:
        sleep(100)
        def changedFilePaths = filesWatcher.getChangedFilePaths()
        changedFilePaths.contains(file_1)
        changedFilePaths.contains(file_2)
        changedFilePaths.contains(file_3)
        changedFilePaths.contains(file_4_sub)

        Files.delete(file_1)
        Files.delete(file_2)
        Files.delete(file_3)
        Files.delete(file_4_sub)
    }

    def "FilesWatcher does not detect creation of directories"() {
        given:
        def filesWatcher = new FilesWatcher(Path.of("C:\\Users\\Kamil\\IdeaProjects\\FilesWatcher\\folder"));
        new Thread(() -> {
            filesWatcher.watch();
        }).start();

        when:
        Files.createDirectory(file_1)

        then:
        sleep(100)
        def changedFilePaths = filesWatcher.getChangedFilePaths()
        !changedFilePaths.contains(file_1)

        Files.delete(file_1)
    }

    private void createRandomFile(Path path) {
        byte[] data = "dfsdfs".getBytes();
        Files.write(path, data);
    }
}
