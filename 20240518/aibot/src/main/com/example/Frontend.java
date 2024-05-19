package com.example;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Frontend extends JFrame {
    private JTextField urlField;
    private JTextField questionField;
    private JButton runButton;
    private JButton askButton;
    private JTextArea outputArea;

    public Frontend() {
        setTitle("YouTube Playlist Scraper & Chatbot");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // URL input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        urlField = new JTextField();
        inputPanel.add(new JLabel("Enter YouTube Playlist URL: "), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);

        // Chatbot input panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        questionField = new JTextField();
        chatPanel.add(new JLabel("Ask Chatbot: "), BorderLayout.WEST);
        chatPanel.add(questionField, BorderLayout.CENTER);
        askButton = new JButton("Ask");
        askButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askChatbot();
            }
        });
        chatPanel.add(askButton, BorderLayout.EAST);

        // Button panel
        JPanel buttonPanel = new JPanel();
        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runYouTubePlaylistScraper();
            }
        });
        buttonPanel.add(runButton);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(inputPanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.EAST);
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
            YouTubePlaylistScraper.main(new String[]{url});
            outputArea.append("YouTubePlaylistScraper completed.\n");

            // // Run YouTubeVideoDownloader
            // outputArea.append("Running YouTubeVideoDownloader...\n");
            // YouTubeVideoDownloader.main(new String[]{});
            // outputArea.append("YouTubeVideoDownloader completed.\n");

            // // Run MP4ToWAVConverter
            // outputArea.append("Running MP4ToWAVConverter...\n");
            // MP4ToWAVConverter.main(new String[]{});
            // outputArea.append("MP4ToWAVConverter completed.\n");

            // // Run AudioSplitter
            // outputArea.append("Running AudioSplitter...\n");
            // AudioSplitter.main(new String[]{});
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

        // Call the Chatbot with the provided question
        try {
            Chatbot.main(new String[]{question});
            outputArea.append("Chatbot interaction completed.\n");
        } catch (IOException ex) {
            ex.printStackTrace();
            outputArea.append("An error occurred: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frontend().setVisible(true);
            }
        });
    }
}
