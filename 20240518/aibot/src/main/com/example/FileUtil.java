package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FileUtil {
    public static String readFileAsString(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        return String.join("\n", lines);
    }

    public static JSONArray readJsonFileAsArray(String filePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONArray(jsonContent);
    }

    public static JSONObject readJsonFileAsObject(String filePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONObject(jsonContent);
    }

    public static void makeDir(String filePathName) {
        File folder = new File(filePathName);
        if (!folder.exists()) folder.mkdirs();
    }

}
