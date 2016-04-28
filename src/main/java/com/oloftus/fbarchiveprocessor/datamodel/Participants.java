package com.oloftus.fbarchiveprocessor.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Participants {

    private static final String FILE_NAME_SEPARATOR = "-";
    private static final String GROUP_NAME_SEPARATOR = ", ";
    private List<Participant> participants;

    public Participants(Participant... participants) {
        
        this.participants = new ArrayList<>(participants.length);
        
        for (Participant participant : participants) {
            this.participants.add(participant);
        }
    }

    public Participants(List<Participant> participants) {
        
        this.participants = new ArrayList<>(participants.size());
        
        // TODO: Change to avoid verbose code- just copy the list
        for (Participant participant : participants) {
            this.participants.add(participant);
        }
    }
    
    public List<Participant> getMembers() {
        
        // TODO: Return immutable version
        return participants;
    }
    
    public String getGroupId() {

        StringBuilder fileNameSb = new StringBuilder();
        for (Participant participant : participants) {
            fileNameSb.append(participant.getId());
            fileNameSb.append(FILE_NAME_SEPARATOR);
        }

        // Remove trailing -
        String fileName = fileNameSb.substring(0, fileNameSb.length() - FILE_NAME_SEPARATOR.length());

        return fileName;
    }
    
    public String getGroupName(List<String> myNames) {
        
        StringBuilder participantNamesSb = new StringBuilder();
        for (Participant participant : participants) {

            String name = participant.getName();
            if (myNames.contains(name)) {
                continue;
            }

            participantNamesSb.append(name);
            participantNamesSb.append(GROUP_NAME_SEPARATOR);
        }

        // Remove trailing , (space)
        int end = participantNamesSb.length() - GROUP_NAME_SEPARATOR.length();
        String groupName = participantNamesSb.substring(0, end > 0 ? end : 0);

        return groupName;
    }
    
    // TODO: Return immutable list instead
    public List<Participant> asList() {
        
        return participants;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((participants == null) ? 0 : participants.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Participants other = (Participants) obj;
        if (participants == null) {
            if (other.participants != null)
                return false;
        }
        else if (!participants.equals(other.participants))
            return false;
        return true;
    }
}
