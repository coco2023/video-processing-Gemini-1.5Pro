package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerUtil {

    public static List<List<String>> extractAllLists(String text) {
        System.out.println(text);
        List<List<String>> allLists = new ArrayList<>();
          String patternString = "\\[(.*?)\\]";
          Pattern pattern = Pattern.compile(patternString);
          Matcher matcher = pattern.matcher(text);
  
          while (matcher.find()) {
            //   allLists.add(matcher.group(1).trim());
            String content = matcher.group(1).trim();
            List<String> subList = extractItems(content);
            allLists.add(subList);
          }
  
          return allLists;
      }

      public static List<String> extractItems(String content) {
        List<String> itemsList = new ArrayList<>();
        String itemPatternString = "\"(.*?)\"|'(.*?)'"; // "\"(.*?)\"";
        Pattern itemPattern = Pattern.compile(itemPatternString);
        Matcher itemMatcher = itemPattern.matcher(content);

        // while (itemMatcher.find()) {
        //     itemsList.add(itemMatcher.group(1).trim());
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

    // public static void main(String[] args) {
    //     String text = "this is a long pain text this is the answer: The speech text is about the importance of small businesses, the challenges they face in accessing credit, and the Federal Reserve's efforts to support them. " +
    //             "`referList`: [" +
    //             "\"Small businesses are essential to creating jobs in our economy they employ roughly one half of all Americans and accounts for about 60% of gross job creation\"," +
    //             "\"newer small businesses less than two years old are especially important over the past 20 years these start-up Enterprises accounted for roughly one-quarter of gross job creation even though they employ collectively less than 10% of the workforce\"," +
    //             "\"the formation and growth of small businesses depends critically on access to credit unfortunately those\"," +
    //             "\"census report that credit conditions remain very difficult for example the net percentage of survey respondents telling the national Federation of Independent Business that credit conditions have Titan over the prior three months has remained extremely elevated by historical standards\"," +
    //             "\"clearly do to support the recovery we need to find ways to ensure the credit-worthy borrowers have access to needed loans\"," +        
    //             "\"over the past two years the Federal Reserve and other agencies have made a concerted effort to stabilize our financial system in our economy\"," +
    //             "\"these efforts importantly have included working to facilitate the flow of credit to viable small businesses\"" +
    //             "] " +
    //             "`idList`: [" +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#3#00:00:25.000#00:00:34.700'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#3#00:00:35.600#00:00:50.100'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#3#00:00:52.000#00:00:58.800'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#4#00:00:00.000#00:00:15.500'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#4#00:00:50.000#00:00:57.400'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#5#00:00:00.500#00:00:07.200'," +
    //             "'1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#5#00:00:08.400#00:00:14.300'" +
    //             "]";

    //     List<List<String>> allLists = extractAllLists(text);

    //     System.out.println("Extracted Lists:");
    //     for (List<String> list : allLists) {
    //         System.out.println(list);
    //     }
    // }
}