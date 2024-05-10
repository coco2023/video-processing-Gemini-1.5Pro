package com.prac.video1.youtube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class VideoDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(VideoDetailsService.class);
    private final DataSource dataSource;

    public VideoDetailsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void storeVideoDetails(String videoId, String title, String description, String captions) throws Exception {
        logger.info("Storing video: {} | {}", title, videoId);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO videos (video_id, title, description, captions) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description), captions = VALUES(captions)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, videoId);
                pstmt.setString(2, title);
                pstmt.setString(3, description);
                pstmt.setString(4, captions);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("Error storing video data", e);
                e.printStackTrace();
            }
        }
    }
}
