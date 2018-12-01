package com.dictionary.audio.audiodictionary;

class Item implements Comparable<Item> {
    String sentence;
    int votes, index;
    String definition, recordingId;

    public Item(String sentence, int index, int votes, String definition, String recordingId) {
        this.sentence = sentence;
        this.index = index;
        this.definition = definition;
        this.recordingId = recordingId;
        this.votes = votes;
    }

    public String getSentence() {
        return sentence;
    }

    public int getIndex() {
        return index;
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

    public int getVotes() {
        return votes;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public int compareTo(Item o) {
        return o.index - this.index;
    }
}
