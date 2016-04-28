package com.oloftus.fbarchiveprocessor;

import java.io.File;
import java.util.List;

public class GuiCommunicator {

    public static enum OutputFormat {
        HTML, TEXT
    }

    File inputArchive;
    File outputDirectory;
    GuiCommunicator.OutputFormat outputFormat;
    List<String> participants;
    List<String> myNames;

    Runnable goAction;
    Runnable parseDoneAction;
    Runnable confirmMyNameAction;
    Runnable writeThreadsDoneAction;

    public Runnable getWriteThreadsDoneAction() {

        return writeThreadsDoneAction;
    }

    public void setWriteThreadsDoneAction(Runnable writeThreadsDoneAction) {

        this.writeThreadsDoneAction = writeThreadsDoneAction;
    }

    public List<String> getMyNames() {

        return myNames;
    }

    public void setMyNames(List<String> myNames) {

        this.myNames = myNames;
    }

    public Runnable getConfirmMyNameAction() {

        return confirmMyNameAction;
    }

    public void setConfirmMyNameAction(Runnable confirmMyNameAction) {

        this.confirmMyNameAction = confirmMyNameAction;
    }

    public List<String> getParticipants() {

        return participants;
    }

    public void setParticipants(List<String> participants) {

        this.participants = participants;
    }

    public Runnable getParseDoneAction() {

        return parseDoneAction;
    }

    public void setParseDoneAction(Runnable parseDoneAction) {

        this.parseDoneAction = parseDoneAction;
    }

    public Runnable getGoAction() {

        return goAction;
    }

    public void setGoAction(Runnable goAction) {

        this.goAction = goAction;
    }

    public File getInputArchive() {

        return inputArchive;
    }

    public void setInputArchive(File inputArchive) {

        this.inputArchive = inputArchive;
    }

    public File getOutputDirectory() {

        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {

        this.outputDirectory = outputDirectory;
    }

    public GuiCommunicator.OutputFormat getOutputFormat() {

        return outputFormat;
    }

    public void setOutputFormat(GuiCommunicator.OutputFormat outputFormat) {

        this.outputFormat = outputFormat;
    }
}