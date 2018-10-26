package ris58h.goleador.worker;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public String fetchUnprocessedVideoId() throws Exception {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(" SELECT video_id " +
                        " FROM video " +
                        " WHERE times IS NULL AND error IS NULL " +
                        " LIMIT 1 ");
        ) {
            String videoId = rs.next() ? rs.getString(1) : null;
            return videoId;
        }
    }

    public void updateVideoTimes(String videoId, List<Integer> times) throws Exception {
        String timeString = times.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET times = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, timeString);
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
