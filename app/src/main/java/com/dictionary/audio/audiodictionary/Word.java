package com.dictionary.audio.audiodictionary;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Word {

    public String uid;
    public String word;
    public List<String> sentences;
    //public Map<String, Integer> recordings;
    public List<Pair<String, Integer>> recordings;
    public List<String> definitions;

    public Word() { }

    public Word(String uid, String word, List<String> sentences, List<Pair<String, Integer>> recordings, //Map<String, Integer> recordings,
                List<String> definitions) {
        this.uid = uid;
        this.word = word;
        this.sentences = sentences;
        this.recordings = recordings;
        this.definitions = definitions;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("word", word);
        result.put("sentences", sentences);
        result.put("recordings", recordings);
        result.put("definition", definitions);

        return result;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public List<String> getSentences() {
        return sentences;
    }

    /*
    public Map<String, Integer> getRecordings() {
        return recordings;
    }*/

    public List<Pair<String, Integer>> getRecordings() {
        return recordings;
    }

    public String getUid() {
        return uid;
    }

    public String getWord() {
        return word;
    }
}