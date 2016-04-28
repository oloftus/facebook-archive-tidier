package com.oloftus.fbarchiveprocessor.conversationswriters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.oloftus.fbarchiveprocessor.Utils;
import com.oloftus.fbarchiveprocessor.datamodel.Message;
import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;
import com.oloftus.fbarchiveprocessor.datamodel.Participant;

public class HtmlConversationsWriter extends AbstractConversationsWriter {

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormat.forPattern("dd MMMM yyyy HH:mm");

    private static final String STYLES_CSS_RESOURCE_FILE_NAME = "styles.css";
    private static final String STYLES_CSS_OUTPUT_FILE_NAME = STYLES_CSS_RESOURCE_FILE_NAME;
    private static final String NO_THREAD_SELECTED_RESOURCE_FILE_NAME = "no_thread_selected.html";
    private static final String NO_THREAD_SELECTED_OUTPUT_FILE_NAME = NO_THREAD_SELECTED_RESOURCE_FILE_NAME;
    private static final String FRAMESET_RESOURCE_FILE_NAME = "frameset.html";
    private static final String FRAMESET_OUTPUT_FILE_NAME = "messages.html";
    private static final String CATALOGUE_FILE_NAME = "catalogue.html";

    private static final String HTML_DIRECTORY = "html";
    private static final String THREADS_DIRECTORY = "threads";
    private static final String FILE_EXTENSION = ".html";

    private static final String META_CHARSET = "<meta charset=\"UTF-8\" />";
    private static final String THREADS_STYLESHEET_INCLUDE = "<link href=\"../styles.css\" type=\"text/css\" rel=\"stylesheet\" />";
    private static final String CATALOGUE_STYLESHEET_INCLUDE = "<link href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\" />";
    private static final String HTML_PREAMBLE =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">";

    private File outputDirectory;
    private Path htmlDirectory;
    private List<MessageThread> threads;
    private List<String> myNames;

    public HtmlConversationsWriter(File outputDirectory, List<MessageThread> threads, List<String> myNames) {

        this.outputDirectory = outputDirectory;
        this.threads = threads;
        this.myNames = myNames;
    }

    @Override
    public void writeMessageThreads() {

        htmlDirectory = createHtmlDirectory();
        createThreadsDirectory();

        writeThreads();
        writeCatalogue();

        copyResourceToDirectory(FRAMESET_RESOURCE_FILE_NAME, outputDirectory.toString(), FRAMESET_OUTPUT_FILE_NAME);
        copyResourceToDirectory(NO_THREAD_SELECTED_RESOURCE_FILE_NAME, htmlDirectory.toString(),
                NO_THREAD_SELECTED_OUTPUT_FILE_NAME);
        copyResourceToDirectory(STYLES_CSS_RESOURCE_FILE_NAME, htmlDirectory.toString(), STYLES_CSS_OUTPUT_FILE_NAME);
    }

    private Path createHtmlDirectory() {

        String htmlDirectoryPath = Utils.joinPathWithSeparator(outputDirectory.toString(), HTML_DIRECTORY);
        Path htmlDirectory = Paths.get(htmlDirectoryPath);
        try {
            Files.createDirectory(htmlDirectory);
        }
        catch (IOException ex) {
            // TODO: Log failure
            System.err.println(ex);
        }

        return htmlDirectory;
    }

    private void writeCatalogue() {

        String catalogueFilePathStr = Utils.joinPathWithSeparator(htmlDirectory.toString(), CATALOGUE_FILE_NAME);
        Path catalogueFilePath = Paths.get(catalogueFilePathStr);

        Path catalogueFile = null;
        try {
            catalogueFile = Files.createFile(catalogueFilePath);
        }
        catch (IOException ex) {
            // TODO: Log failure
            System.err.println(ex);
        }

        writeToFile(catalogueFile,
                HTML_PREAMBLE +
                        "<head>" +
                        META_CHARSET +
                        CATALOGUE_STYLESHEET_INCLUDE +
                        "<title>Message catalogue</title>" +
                        "</head>" +
                        "<body>" +
                        "<h1 id=\"app-title\">Messenger</h1>" +
                        "<ul id=\"catalogue\">");

        for (MessageThread thread : threads) {
            String linkHref = THREADS_DIRECTORY + "/" + getThreadFileName(thread);

            writeToFile(catalogueFile,
                    "<li>" +
                            "<a href=\"" + linkHref + "\" target=\"thread\">" +
                            thread.getParticipants().getGroupName(myNames) +
                            "</a>" +
                            "</li>");
        }

        writeToFile(catalogueFile,
                "</ul>" +
                        "</body>" +
                        "</html>");
    }

    private void copyResourceToDirectory(String resourceFileName, String outputDirectoryStr, String outputFileName) {

        URL frameSetSource = getClass().getClassLoader().getResource(resourceFileName);
        String frameSetFileName = Utils.joinPathWithSeparator(outputDirectoryStr, outputFileName);
        Path frameSetFile = Paths.get(frameSetFileName);

        try {
            Files.copy(frameSetSource.openStream(), frameSetFile);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeThreads() {

        for (MessageThread thread : threads) {
            Path threadFile = createThreadFile(thread);

            String groupName = thread.getParticipants().getGroupName(myNames);

            writeToFile(threadFile,
                    HTML_PREAMBLE +
                            "<head>" +
                            THREADS_STYLESHEET_INCLUDE +
                            META_CHARSET +
                            "<title>" + groupName + "</title>" +
                            "</head>" +
                            "<body>" +
                            "<h1>" + groupName + "</h1>" +
                            "<div id=\"messages\">");

            Participant lastMessageSender = null;
            StringBuilder htmlFragmentSb = new StringBuilder();
            
            for (Message message : thread.getMessages()) {
                if (StringUtils.isBlank(message.getContent())) {
                    continue;
                }

                Participant sender = message.getSender();
                boolean isNewGroup = lastMessageSender == null || !lastMessageSender.equals(sender);
                String meClass = sender.isMe(myNames) ? " me" : "";
                String newGroupClass = isNewGroup ? " group" : "";
                
                htmlFragmentSb.append("<div class=\"message" + meClass + newGroupClass + "\">");
                
                if (isNewGroup && !sender.isMe(myNames) && thread.getParticipants().getMembers().size() > 2) {
                    htmlFragmentSb.append("<span class=\"sender\">" + sender.getName() + "</span>");
                }
                
                htmlFragmentSb.append("<span class=\"content\">" + message.getContent() + "</span>");
                
                htmlFragmentSb.append("<span class=\"date\">" + OUTPUT_DATE_FORMAT.print(message.getDateSent()) + "</span>");
                htmlFragmentSb.append("</div>");

                writeToFile(threadFile, htmlFragmentSb.toString());
                htmlFragmentSb.setLength(0); // Reset

                lastMessageSender = message.getSender();
            }

            writeToFile(threadFile,
                    "</div>" +
                            "</body>" +
                            "</html>");
        }
    }

    @Override
    protected String getThreadsDirectoryPath() {

        return Utils.joinPathWithSeparator(htmlDirectory.toString(), THREADS_DIRECTORY);
    }

    @Override
    protected String getThreadsFileExtension() {

        return FILE_EXTENSION;
    }
}
