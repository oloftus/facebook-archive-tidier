package com.oloftus.fbarchiveprocessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;

public class ArchiveProcessor {

    private static final String MESSAGES_RELATIVE_PATH = Utils.joinPathWithSeparator("html", "messages.htm");

    private Path expandedArchive;
    private Conversations conversations = new Conversations();

    public ArchiveProcessor(Path expandedArchive) {

        this.expandedArchive = expandedArchive;
    }

    public ArrayList<MessageThread> processMessagesHtml() throws ParserConfigurationException, SAXException, IOException {

        String messagesHtmlPath = expandedArchive.toString() + MESSAGES_RELATIVE_PATH;
        
        ArrayList<MessageThread> threads = parseMessagesHtml(messagesHtmlPath);
        ArrayList<MessageThread> sortedConversations = sortMessagesAndThreads(threads);
        
        return sortedConversations;
    }

    private ArrayList<MessageThread> parseMessagesHtml(String messagesPath) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new MessagesHtmlSaxHandler(conversations);
        saxParser.parse(messagesPath, handler);
        
        return new ArrayList<>(conversations.getThreads());
    }

    private ArrayList<MessageThread> sortMessagesAndThreads(ArrayList<MessageThread> threads) {

        for (MessageThread thread : threads) {
            Collections.sort(thread.getMessages());
            Collections.reverse(thread.getMessages());
        }

        Collections.sort(threads);
        Collections.reverse(threads);

        return threads;
    }
}
