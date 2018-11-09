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
    public Map<String, Integer> recordings;
    public String definition;

    public Word() { }

    public Word(String uid, String word, List<String> sentences, Map<String, Integer> recordings, String definition) {
        this.uid = uid;
        this.word = word;
        this.sentences = sentences;
        this.recordings = recordings;
        this.definition = definition;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("word", word);
        result.put("sentences", sentences);
        result.put("recordings", recordings);
        result.put("definition", definition);

        return result;
    }

}