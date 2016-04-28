package com.oloftus.fbarchiveprocessor.datamodel;

import org.joda.time.DateTime;

public class Message implements Comparable<Message> {

    private Participant sender;
    private DateTime dateSent;
    private String content;
    private String partialName;

    public String getPartialName() {

        return partialName;
    }

    public void setPartialName(String partialName) {

        this.partialName = partialName;
    }

    public void setSender(Participant sender) {

        // System.out.println("Added sender: " + sender.getName());

        this.sender = sender;
    }

    public void setDateSent(DateTime dateSent) {

        // System.out.println("Added date sent: " + dateSent);

        this.dateSent = dateSent;
    }

    public void setContent(String content) {

        // System.out.println("Added message content: " + content);

        this.content = content;
    }

    public Participant getSender() {

        return sender;
    }

    public DateTime getDateSent() {

        return dateSent;
    }

    public String getContent() {

        return content;
    }

    @Override
    public int compareTo(Message message) {

        return this.getDateSent().compareTo(message.getDateSent());
    }
}
