package com.dictionary.audio.audiodictionary;

class Item {
    String sentence;
    int recording;
    String definition, recordingId;

    public Item(String sentence, int recording, String definition, String recordingId) {
        this.sentence = sentence;
        this.recording = recording;
        this.definition = definition;
        this.recordingId = recordingId;
    }

    public String getSentence() {
        return sentence;
    }

    public int getRecording() {
        return recording;
    }

    public String getDefinition() {
        return definition;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setRecording(int recording) {
        this.recording = recording;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
