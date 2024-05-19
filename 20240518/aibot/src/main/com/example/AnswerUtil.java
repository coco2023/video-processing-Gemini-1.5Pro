package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerUtil {

    public static List<List<String>> extractAllLists(String text) {
        System.out.println("This is the chatbot output: " + text);
        List<List<String>> allLists = new ArrayList<>();
        String patternString = "\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // allLists.add(matcher.group(1).trim());
            String content = matcher.group(1).trim();
            System.out.println("content: " + content);
            List<String> subList = extractItems(content);
            allLists.add(subList);
        }
        System.out.println("content is Empty? " + allLists.isEmpty());

        return allLists;
    }

    public static List<String> extractItems(String content) {
        List<String> itemsList = new ArrayList<>();
        String itemPatternString = "\"(.*?)\"|'(.*?)'"; // "\"(.*?)\"";
        Pattern itemPattern = Pattern.compile(itemPatternString);
        Matcher itemMatcher = itemPattern.matcher(content);

        // while (itemMatcher.find()) {
        // itemsList.add(itemMatcher.group(1).trim());
        // }
        while (itemMatcher.find()) {
            if (itemMatcher.group(1) != null) {
                itemsList.add(itemMatcher.group(1).trim());
            } else if (itemMatcher.group(2) != null) {
                itemsList.add(itemMatcher.group(2).trim());
            }
        }

        return itemsList;
    }

    public static List<String> extractSentenceIds(List<String> list) {
        List<String> ids = new ArrayList<>();
        String regex = "^(.*?\\|.*?)$";
        Pattern pattern = Pattern.compile(regex);

        for (String item : list) {
            Matcher matcher = pattern.matcher(item);
            if (matcher.matches()) {
                String[] parts = item.split("\\|");
                if (parts.length > 1) {
                    ids.add(parts[0]);
                }
            } else {
                return list;
            }
        }
        return ids;
    }
}