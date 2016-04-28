package com.oloftus.fbarchiveprocessor;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.oloftus.fbarchiveprocessor.GuiCommunicator.OutputFormat;
import com.oloftus.fbarchiveprocessor.conversationswriters.ConversationsWriter;
import com.oloftus.fbarchiveprocessor.conversationswriters.HtmlConversationsWriter;
import com.oloftus.fbarchiveprocessor.conversationswriters.TextConversationsWriter;
import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;
import com.oloftus.fbarchiveprocessor.datamodel.Participant;
import com.oloftus.fbarchiveprocessor.gui.MainWindow;

public class Main {

    private static ArrayList<MessageThread> threads;

    private static GuiCommunicator guiCommunicator;

    public static void main(String[] args) {

        setupGuiCommunicator();
        startGuiThread();
    }

    private static void setupGuiCommunicator() {

        guiCommunicator = new GuiCommunicator();

        guiCommunicator.setGoAction(new Runnable() {

            @Override
            public void run() {

                startExpandParseThread();
            }
        });

        guiCommunicator.setConfirmMyNameAction(new Runnable() {

            @Override
            public void run() {

                startWriteThread();
            }
        });
    }

    private static synchronized void startExpandParseThread() {

        Thread thread = new Thread() {

            @Override
            public void run() {

                File outputDirectory = guiCommunicator.getOutputDirectory();
                File inputArchive = guiCommunicator.getInputArchive();

                Path expandedArchive = expandArchive(outputDirectory, inputArchive);
                threads = processMessageHtml(expandedArchive);
                deleteExpandedArchive(expandedArchive);

                guiCommunicator.setParticipants(getAllParticipants());
                guiCommunicator.getParseDoneAction().run();
            }
        };

        thread.start();
    }

    private static List<String> getAllParticipants() {

        Set<String> participantsSet = new HashSet<>();
        for (MessageThread thread : threads) {
            for (Participant participant : thread.getParticipants().getMembers()) {
                participantsSet.add(participant.getName());
            }
        }

        String[] participantsArr = new String[participantsSet.size()];
        participantsSet.toArray(participantsArr);
        List<String> participantsList = Arrays.asList(participantsArr);
        Collections.sort(participantsList);

        return participantsList;
    }

    private static void startWriteThread() {

        Thread thread = new Thread() {

            @Override
            public void run() {

                File outputDirectory = guiCommunicator.getOutputDirectory();
                OutputFormat outputFormat = guiCommunicator.getOutputFormat();
                List<String> myNames = guiCommunicator.getMyNames();

                writeMessageThreads(outputDirectory, threads, outputFormat, myNames);
                guiCommunicator.getWriteThreadsDoneAction().run();
            }
        };

        thread.start();
    }

    private static void deleteExpandedArchive(Path expandedArchive) {

        try {
            FileUtils.deleteDirectory(expandedArchive.toFile());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void writeMessageThreads(File outputDirectory, ArrayList<MessageThread> threads,
            OutputFormat outputFormat, List<String> myNames) {

        ConversationsWriter conversationWriter = null;

        switch (outputFormat) {
            case HTML:
                conversationWriter = new HtmlConversationsWriter(outputDirectory, threads, myNames);
                break;
            case TEXT:
                conversationWriter = new TextConversationsWriter(outputDirectory, threads, myNames);
                break;
        }

        conversationWriter.writeMessageThreads();
    }

    private static ArrayList<MessageThread> processMessageHtml(Path expandedArchive) {

        ArrayList<MessageThread> threads = null;

        try {
            threads = new ArchiveProcessor(expandedArchive).processMessagesHtml();
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return threads;
    }

    private static Path expandArchive(File outputDirectory, File inputArchive) {

        Path expandedArchive = null;

        try {
            expandedArchive = new ArchiveUnzipper(inputArchive, outputDirectory).expandArchive();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return expandedArchive;
    }

    private static void startGuiThread() {

        EventQueue.invokeLater(new Runnable() {

            public void run() {

                MainWindow mainWindow = new MainWindow(guiCommunicator);
                mainWindow.setVisible(true);
            }
        });
    }
}
