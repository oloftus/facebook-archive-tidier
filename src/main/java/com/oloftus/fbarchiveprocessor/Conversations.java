package com.oloftus.fbarchiveprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.oloftus.fbarchiveprocessor.datamodel.Message;
import com.oloftus.fbarchiveprocessor.datamodel.Participant;
import com.oloftus.fbarchiveprocessor.datamodel.Participants;
import com.oloftus.fbarchiveprocessor.datamodel.MessageThread;

public class Conversations {

    private HashMap<String, Participant> participants = new HashMap<>(); // Name-Participants
    private HashMap<Participants, MessageThread> conversations = new HashMap<>();

    public void addThread(MessageThread thread) {

        conversations.put(thread.getParticipants(), thread);
    }

    public Participant getParticipant(String name) {

        Participant participant = participants.get(name);

        if (participant == null) {
            int id = participants.size();
            participant = new Participant(name, id);
            participants.put(name, participant);
        }

        return participant;
    }

    public Collection<MessageThread> getThreads() {

        return conversations.values();
    }
    
    public MessageThread newThread() {
        
        return new MessageThread();
    }
    
    public void addParticipantsToThread(MessageThread thread, String... participantNames) {
        
        List<Participant> participantsList = new ArrayList<>(participantNames.length);
        for (String name : participantNames) {
            Participant participant = getParticipant(name);
            participantsList.add(participant);
        }
        
        Participants participants = new Participants(participantsList);
        thread.setParticipants(participants);
    }
    
    public void addThreadToConversation(MessageThread thread) {
        
        addThread(thread);
    }
    
    public void newMessageInThread(MessageThread thread) {
        
        Message message = new Message();
        thread.addMessage(message);
    }
    
    public void addPartialSenderToMessage(Message message, String partialSenderName) {

        message.setPartialName(partialSenderName);
    }
    
    public void addSenderToMessage(Message message, String senderName) {
        
        Participant sender = getParticipant(senderName);
        message.setSender(sender);
    }
    
    public void addDateSentToMessage(Message message, DateTime dateSent) {
        
        message.setDateSent(dateSent);
    }
    
    public void addContentToMessage(Message message, String content) {
        
        String messageContent = message.getContent();
        
        if (messageContent != null) {
            message.setContent(messageContent + content);
        }
        else {
            message.setContent(content);
        }
    }
}
