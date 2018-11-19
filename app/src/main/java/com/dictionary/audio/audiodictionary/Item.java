package com.dictionary.audio.audiodictionary;

class Item {
    String sentence;
    int recording;
    String definition;

    public Item(String sentence, int recording, String definition) {
        this.sentence = sentence;
        this.recording = recording;
        this.definition = definition;
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

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setRecording(int recording) {
        this.recording = recording;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
