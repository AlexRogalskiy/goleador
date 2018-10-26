package ris58h.goleador.commenter;

import java.sql.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataAccess {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DataAccess(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void processUncommentedVideos(Consumer<VideoTimes> callback) throws Exception {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.setFetchSize(50);
                try (ResultSet rs = statement.executeQuery(" SELECT video_id, times " +
                        " FROM video " +
                        " WHERE times <> '' AND comment_id IS NULL ")) {
                    while (rs.next()) {
                        String videoId = rs.getString(1);
                        String timesString = rs.getString(2);
                        if (!timesString.isEmpty()) {
                            List<Integer> times = Stream.of(timesString.split(","))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            callback.accept(new VideoTimes(videoId, times));
                        }
                    }
                }
            }
        }
    }

    public void updateCommentId(String videoId, String commentId) throws Exception {
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET comment_id = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, commentId);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }

    public void updateError(String videoId, String error) throws Exception {
        if (error.length() > 255) {
            error = error.substring(0, 255);
        }
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET error = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, error);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }
}
