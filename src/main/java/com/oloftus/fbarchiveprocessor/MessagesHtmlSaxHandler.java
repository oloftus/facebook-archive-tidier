package com.oloftus.fbarchiveprocessor;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;

public class MessagesHtmlSaxHandler extends DefaultHandler {

    private enum ThreadStates {
        INITIAL, PRE_THREADS, THREADS, THREAD, MESSAGE
    }

    private enum MessageStates {
        INITIAL, SENDER, DATE, CONTENT
    }

    private ThreadStates threadsState = ThreadStates.INITIAL;
    private MessageStates messageState = MessageStates.INITIAL;
    private MessageThread currentThread;
    private Conversations conversations;

    public MessagesHtmlSaxHandler(Conversations conversations) {

        super();
        this.conversations = conversations;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        startElemStateMachine(qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName,
            String qName) throws SAXException {

        endElemStateMachine(qName);
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        char[] chars = Arrays.copyOfRange(ch, start, start + length);
        String charStr = String.valueOf(chars);

        if (StringUtils.isBlank(charStr)) {
            return;
        }

        charStateMachine(charStr);
    }

    @SuppressWarnings("incomplete-switch")
    private void startElemStateMachine(String qName, Attributes attributes) {

        switch (threadsState) {
            case INITIAL:
                if (initialToPreThreadsCond(attributes)) {
                    threadsState = ThreadStates.PRE_THREADS;
                }
                break;

            case PRE_THREADS:
                if (preThreadsToThreadsCond(qName)) {
                    threadsState = ThreadStates.THREADS;
                }
                break;

            case THREADS:
                if (threadsToThreadCond(attributes)) {
                    threadsState = ThreadStates.THREAD;
                    newThreadAction();
                }
                break;

            case THREAD:
                if (threadToMessageCond(attributes)) {
                    threadsState = ThreadStates.MESSAGE;
                    newMessageAction();
                }
                break;

            case MESSAGE:
                switch (messageState) {
                    case INITIAL:
                        if (initialToSenderCond(attributes)) {
                            messageState = MessageStates.SENDER;
                        }
                        break;

                    case SENDER:
                        if (senderToDateCond(attributes)) {
                            messageState = MessageStates.DATE;
                        }
                        break;

                    case DATE:
                        if (dateToContentCond(qName)) {
                            messageState = MessageStates.CONTENT;
                        }
                        break;
                }

                break;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void endElemStateMachine(String qName) {

        switch (threadsState) {
            case MESSAGE:
                if (messageState == MessageStates.SENDER) {
                    addSenderToMessageAction();
                }
                else if (messageToThreadCond(qName)) {
                    threadsState = ThreadStates.THREAD;
                    messageState = MessageStates.INITIAL;
                }
                break;
            case THREAD:
                if (threadToThreadsCond(qName)) {
                    saveThreadAction();
                    threadsState = ThreadStates.THREADS;
                }
                break;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void charStateMachine(String charStr) {

        switch (threadsState) {
            case THREAD:
                addParticipantsToThreadAction(charStr);
                break;

            case MESSAGE:
                switch (messageState) {
                    case SENDER:
                        addSenderPartialNameToMessageAction(charStr);
                        break;

                    case DATE:
                        addDateTimeSentToMessageAction(charStr);
                        break;

                    case CONTENT:
                        addContentToMessageAction(charStr);
                        break;
                }

                break;
        }
    }

    /** Conditions **/
    private boolean threadToThreadsCond(String qName) {

        return qName.equals("div");
    }

    private boolean dateToContentCond(String qName) {

        return qName.equals("p");
    }

    private boolean messageToThreadCond(String qName) {

        return qName.equals("p");
    }

    private boolean senderToDateCond(Attributes attributes) {

        String className = attributes.getValue("class");
        return className != null && className.equals("meta");
    }

    private boolean initialToSenderCond(Attributes attributes) {

        String className = attributes.getValue("class");
        return className != null && className.equals("user");
    }

    private boolean threadToMessageCond(Attributes attributes) {

        String className = attributes.getValue("class");
        return className != null && className.equals("message");
    }

    private boolean threadsToThreadCond(Attributes attributes) {

        String className = attributes.getValue("class");
        return className != null && className.equals("thread");
    }

    private boolean preThreadsToThreadsCond(String qName) {

        return threadToThreadsCond(qName);
    }

    private boolean initialToPreThreadsCond(Attributes attributes) {

        String className = attributes.getValue("class");
        return className != null && className.equals("contents");
    }

    private void newThreadAction() {

        currentThread = new MessageThread();
    }

    private void newMessageAction() {

        conversations.newMessageInThread(currentThread);
    }

    private void saveThreadAction() {

        conversations.addThreadToConversation(currentThread);
    }

    private void addContentToMessageAction(String charStr) {

        conversations.addContentToMessage(currentThread.getLastMessage(), charStr);
    }

    private void addDateTimeSentToMessageAction(String charStr) {

        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("EEEE, d MMMM yyyy 'at' H:mm zZ").getParser(),
                DateTimeFormat.forPattern("EEEE, d MMMM yyyy 'at' H:mm z").getParser()
        };
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
        DateTime dateTime = DateTime.parse(charStr, formatter);
        conversations.addDateSentToMessage(currentThread.getLastMessage(), dateTime);
    }

    private void addSenderPartialNameToMessageAction(String charStr) {

        conversations.addSenderToMessage(currentThread.getLastMessage(), charStr);
    }

    private void addParticipantsToThreadAction(String charStr) {

        String[] participantNames = charStr.trim().split(", ");
        conversations.addParticipantsToThread(currentThread, participantNames);
    }
}