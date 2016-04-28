package com.oloftus.fbarchiveprocessor.datamodel;

import java.util.LinkedList;
import java.util.List;

public class MessageThread implements Comparable<MessageThread> {

    private Participants participants;
    private List<Message> messages = new LinkedList<>();

    public List<Message> getMessages() {

        return messages;
    }

    public void addMessage(Message message) {

        messages.add(message);
    }

    public Message getLastMessage() {

        return messages.get(messages.size() - 1);
    }

    public Participants getParticipants() {

        return participants;
    }

    public void setParticipants(Participants participants) {

        this.participants = participants;
    }

    @Override
    public int compareTo(MessageThread thread) {

        return this.getLastMessage().compareTo(thread.getLastMessage());
    }
}
