package com.oloftus.fbarchiveprocessor.datamodel;

import java.util.List;

public class Participant {

    private String name;
    private int id;

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public Participant(String name, int id) {

        super();
        this.name = name;
        this.id = id;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Participant other = (Participant) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public boolean isMe(List<String> myNames) {

        return myNames.contains(name);
    }
}
