package com.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.FileUtil;

public class FindSentences {

    public static String filePath = "train/transcriptions.json";

    public static List<String> extractReferList(String output) {
          List<String> referList = new ArrayList<>();
          Pattern pattern = Pattern.compile("referList = \\[(.*?)\\]", Pattern.DOTALL);
          Matcher matcher = pattern.matcher(output);
          if (matcher.find()) {
                    String listContent = matcher.group(1).trim();
                    String[] items = listContent.split("\",\\s*\"");
                    for (String item : items) {
                              item = item.replaceAll("^\"|\"$", ""); // Remove leading and trailing quotes
                              referList.add(item);
                          }
                    }
          return referList;
    }
    
    public static List<String> matchTranscript(List<String> referList) {
         List<String> resultKey = new ArrayList<>();

          try {
            // Read JSON file
            JSONArray jsonContent = FileUtil.readJsonFileAsArray(filePath);

            // Find sentences
            List<Match> matches = findSentences(jsonContent, referList);
            
            // Print matches
            for (Match match : matches) {
                // System.out.println("Sentence: " + match.sentence);
                // System.out.println("Found in Segment: " + match.segment);
                // System.out.println("Segment Key: " + match.key);
                // System.out.println();
                resultKey.add(match.key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the file.");
        }       
        return resultKey;
    }

    public static List<Match> findSentences(JSONArray jsonArray, List<String> referList) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);
            for (String key : entry.keySet()) {
                String segment = entry.getString(key);
                for (String sentence : referList) {
                    if (segment.contains(sentence)) {
                        matches.add(new Match(sentence, segment, key));
                    }
                }
            }
        }
        return matches;
    }

    public static class Match {
        String sentence;
        String segment;
        String key;

        public Match(String sentence, String segment, String key) {
            this.sentence = sentence;
            this.segment = segment;
            this.key = key;
        }
    }

    public static List<KeyComponents> extraKeyComponents(List<String> keyList) {
        List<KeyComponents> componentsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^#]+)#\\d+#([^#]+)#segment#(\\d+)#([^#]+)#([^#]+)");

        for (String key : keyList) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String videoId = matcher.group(1);
                String playlistId = matcher.group(2);
                String segmentNum = matcher.group(3);
                String startTime = matcher.group(4);
                String endTime = matcher.group(5);

                componentsList.add(new KeyComponents(videoId, playlistId, segmentNum, startTime, endTime));
            }
        }

        return componentsList;
    }

    public static class KeyComponents {
        String videoId;
        String playlistId;
        String segmentNum;
        String startTime;
        String endTime;

        public KeyComponents(String videoId, String playlistId, String segmentNum, String startTime, String endTime) {
            this.videoId = videoId;
            this.playlistId = playlistId;
            this.segmentNum = segmentNum;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
