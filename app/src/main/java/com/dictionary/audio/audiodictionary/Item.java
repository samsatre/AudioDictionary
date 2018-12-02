package com.dictionary.audio.audiodictionary;

class Item implements Comparable<Item> {
    String sentence;
    int votes, index;
    String definition, recordingId, uid;

    public Item(String sentence, int index, int votes, String definition, String recordingId, String uid) {
        this.sentence = sentence;
        this.index = index;
        this.definition = definition;
        this.recordingId = recordingId;
        this.votes = votes;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public int compareTo(Item o) {
        return o.votes - this.votes;
    }
}
