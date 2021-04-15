package com.emoteville.emotevillespawner.util;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteHelper {
    // Getting only associated strings
    public List<String> getListOfStringsMatchingLastWord(List<String> words, String word) {
        ArrayList<String> a = new ArrayList<>();
        for (String s: words) {
            if (s.contains(word)) {
                a.add(s);
            }
        }
        return a;
    }
}
