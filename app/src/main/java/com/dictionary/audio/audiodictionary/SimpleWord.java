package com.dictionary.audio.audiodictionary;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class SimpleWord {

    public String word;
    public List<String> definitions;

    public SimpleWord() { }

    public SimpleWord(String uid, String word, List<String> sentences, Map<String, Integer> recordings,
                      List<String> definitions) {
        this.word = word;
        this.definitions = definitions;
    }


    public List<String> getDefinitions() {
        return definitions;
    }



    public String getWord() {
        return word;
    }
}