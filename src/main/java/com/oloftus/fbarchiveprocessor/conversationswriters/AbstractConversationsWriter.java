package com.oloftus.fbarchiveprocessor.conversationswriters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.oloftus.fbarchiveprocessor.Utils;
import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;

public abstract class AbstractConversationsWriter implements ConversationsWriter {

    protected abstract String getThreadsDirectoryPath();

    protected abstract String getThreadsFileExtension();

    protected void createThreadsDirectory() {

        Path threadsDirectory = Paths.get(getThreadsDirectoryPath());
        try {
            Files.createDirectory(threadsDirectory);
        }
        catch (IOException ex) {
            // TODO: Log failure
            System.err.println(ex);
        }
    }

    protected Path createThreadFile(MessageThread thread) {

        String threadFilePathStr = Utils.joinPathWithSeparator(getThreadsDirectoryPath(), getThreadFileName(thread));
        Path threadFilePath = Paths.get(threadFilePathStr);

        Path theFile = null;
        try {
            theFile = Files.createFile(threadFilePath);
        }
        catch (IOException ex) {
            // TODO: Log failure
            System.err.println(ex);
        }

        return theFile;
    }

    protected String getThreadFileName(MessageThread thread) {

        return thread.getParticipants().getGroupId() + getThreadsFileExtension();
    }

    protected void writeToFile(Path file, String content) {

        try {
            Files.write(file, content.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        }
        catch (IOException ex) {
            // TODO: Log failure
            System.err.println(ex);
        }
    }
}
