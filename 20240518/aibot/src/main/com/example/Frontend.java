package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Frontend extends JFrame {
    private JTextField urlField;
    private JTextField questionField;
    private JButton runButton;
    private JButton askButton;
    private JTextArea outputArea;
    private JFXPanel jfxPanel;

    public Frontend() {
        setTitle("YouTube Playlist Scraper & Chatbot");
        setSize(1000, 800); // Increased size for video display
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // URL input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 3)); // Changed to GridLayout for better organization
        urlField = new JTextField();
        questionField = new JTextField();

        inputPanel.add(new JLabel("Enter YouTube Playlist URL: "));
        inputPanel.add(urlField);
        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runYouTubePlaylistScraper();
            }
        });
        inputPanel.add(runButton);

        inputPanel.add(new JLabel("Ask Chatbot: "));
        inputPanel.add(questionField);
        askButton = new JButton("Ask");
        askButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askChatbot();
            }
        });
        inputPanel.add(askButton);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // JFXPanel for video
        jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(800, 400)); // Ensure preferred size for video panel

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(jfxPanel, BorderLayout.SOUTH);
    }

    private void runYouTubePlaylistScraper() {
        String url = urlField.getText();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        outputArea.setText("Running YouTubePlaylistScraper with URL: " + url + "\n");

        // Running the YouTubePlaylistScraper.main method with the provided URL
        try {
            YouTubePlaylistScraper.main(new String[] { url });
            outputArea.append("YouTubePlaylistScraper completed.\n");

            // // Run YouTubeVideoDownloader
            // outputArea.append("Running YouTubeVideoDownloader...\n");
            // YouTubeVideoDownloader.main(new String[] {});
            // outputArea.append("YouTubeVideoDownloader completed.\n");

            // // Run MP4ToWAVConverter
            // outputArea.append("Running MP4ToWAVConverter...\n");
            // MP4ToWAVConverter.main(new String[] {});
            // outputArea.append("MP4ToWAVConverter completed.\n");

            // // Run AudioSplitter
            // outputArea.append("Running AudioSplitter...\n");
            // AudioSplitter.main(new String[] {});
            // outputArea.append("AudioSplitter completed.\n");

            // // Run UploadToGCS
            // outputArea.append("Running UploadToGCS...\n");
            // UploadToGCS.main(new String[]{});
            // outputArea.append("UploadToGCS completed.\n");

        } catch (Exception ex) {
            ex.printStackTrace();
            outputArea.append("An error occurred: " + ex.getMessage() + "\n");
        }
    }

    private void askChatbot() {
        String question = questionField.getText();
        if (question.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a question.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        outputArea.setText("Asking chatbot: " + question + "\n");

        // Create a separate thread to handle the chatbot interaction to avoid freezing
        // the UI
        new Thread(() -> {
            try {
                Chatbot.main(new String[] { question });
                SwingUtilities.invokeLater(() -> outputArea.append("Chatbot interaction completed.\n"));

                // Display the generated video
                displayVideo("outputs/mp4merge/merged_video.mp4");
            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> outputArea.append("An error occurred: " + ex.getMessage() + "\n"));
            }
        }).start();
    }

    private void displayVideo(String videoPath) {
        Platform.runLater(() -> {
            try {
                File videoFile = new File(videoPath);
                if (!videoFile.exists()) {
                    outputArea.append("Video file not found: " + videoPath + "\n");
                    return;
                }

                Media media = new Media(videoFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                mediaView.setFitWidth(jfxPanel.getWidth());
                mediaView.setFitHeight(jfxPanel.getHeight());

                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(mediaView);

                Scene scene = new Scene(borderPane);
                jfxPanel.setScene(scene);

                mediaPlayer.setOnReady(() -> {
                    outputArea.append("Video Duration: " + mediaPlayer.getTotalDuration().toMinutes() + " minutes\n");
                    mediaPlayer.play();
                });

                mediaPlayer.setOnError(
                        () -> outputArea.append("Error occurred: " + mediaPlayer.getError().getMessage() + "\n"));

            } catch (Exception e) {
                e.printStackTrace();
                outputArea.append("Exception occurred while trying to play video: " + e.getMessage() + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Frontend().setVisible(true);
        });
    }
}
