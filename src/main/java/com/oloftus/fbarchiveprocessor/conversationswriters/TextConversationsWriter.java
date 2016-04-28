package com.oloftus.fbarchiveprocessor.conversationswriters;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.oloftus.fbarchiveprocessor.Utils;
import com.oloftus.fbarchiveprocessor.datamodel.Message;
import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;

public class TextConversationsWriter extends AbstractConversationsWriter {

    private static final String THREADS_DIRECTORY = "threads";
    private static final String FILE_EXTENSION = ".txt";

    private File outputDirectory;
    private List<MessageThread> threads;
    private List<String> myNames;

    public TextConversationsWriter(File outputDirectory, List<MessageThread> threads, List<String> myNames) {

        this.outputDirectory = outputDirectory;
        this.threads = threads;
        this.myNames = myNames;
    }

    @Override
    public void writeMessageThreads() {

        createThreadsDirectory();
        writeThreads();
    }

    private void writeThreads() {

        for (MessageThread thread : threads) {
            Path file = createThreadFile(thread);

            String groupName = thread.getParticipants().getGroupName(myNames);

            writeToFile(file, groupName + "\n");
            writeToFile(file, "==============\n\n");

            for (Message message : thread.getMessages()) {
                writeToFile(file, message.getSender().getName() + " (" + message.getDateSent().toString() + ")\n");
                writeToFile(file, message.getContent() + "\n\n");
            }

        }
    }

    @Override
    protected String getThreadsDirectoryPath() {

        return Utils.joinPathWithSeparator(outputDirectory.toString(), THREADS_DIRECTORY);
    }

    @Override
    protected String getThreadsFileExtension() {

        return FILE_EXTENSION;
    }
}
