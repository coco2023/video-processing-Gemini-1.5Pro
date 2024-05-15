package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UploadToGCS {
    public static void main(String[] args) {
        String bucketName = "video1-20240502"; // your-bucket-name
        String folderPath = "outputs/videos/wav/";
        String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";

        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        uploadObject(bucketName, file.getName(), file.getAbsolutePath(), jsonPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void uploadObject(String bucketName, String objectName, String filePath, String jsonPath) throws Exception {
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonPath)))
                .build()
                .getService();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

        System.out.println("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
    }
}
