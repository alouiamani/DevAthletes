package org.gymify.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

import org.gymify.utils.gymifyDataBase;
import com.google.gson.Gson;

public class StatsController {
    private static final Logger LOGGER = Logger.getLogger(StatsController.class.getName());

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private Connection connection;

    public StatsController() {
        this.connection = gymifyDataBase.getInstance().getConnection();
    }

    @FXML
    private void initialize() {
        webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/chart.html").toExternalForm());

        // Charger les données après que la page soit prête
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadStats();
            }
        });
    }

    private List<StatData> getStatsData() throws SQLException {
        List<StatData> statsData = new ArrayList<>();
        String query = "SELECT YEAR(post.created_at) AS year, MONTH(post.created_at) AS month, "
                + "COUNT(DISTINCT post.id) AS posts_count, "
                + "COUNT(comment.id) AS comments_count, COUNT(reactions.id) AS reactions_count "
                + "FROM post "
                + "LEFT JOIN comment ON post.id = comment.postId "
                + "LEFT JOIN reactions ON post.id = reactions.postId "
                + "WHERE YEAR(post.created_at) = 2025 "
                + "GROUP BY YEAR(post.created_at), MONTH(post.created_at) "
                + "ORDER BY YEAR(post.created_at), MONTH(post.created_at)";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                int postsCount = rs.getInt("posts_count");
                int commentsCount = rs.getInt("comments_count");
                int reactionsCount = rs.getInt("reactions_count");
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-01");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                statsData.add(new StatData(date, postsCount, commentsCount, reactionsCount));
            }
        }
        return statsData;
    }

    private void loadStats() {
        Platform.runLater(() -> {
            try {
                List<StatData> statsData = getStatsData();
                
                // Préparer les données pour le graphique
                List<String> labels = new ArrayList<>();
                List<Integer> posts = new ArrayList<>();
                List<Integer> comments = new ArrayList<>();
                List<Integer> reactions = new ArrayList<>();

                for (StatData data : statsData) {
                    String monthLabel = String.format("%d-%02d", data.getYear(), data.getMonth());
                    labels.add(monthLabel);
                    posts.add(data.getPostsCount());
                    comments.add(data.getCommentsCount());
                    reactions.add(data.getReactionsCount());
                }

                // Convertir les listes en chaînes JSON
                String labelsJson = new Gson().toJson(labels);
                String postsJson = new Gson().toJson(posts);
                String commentsJson = new Gson().toJson(comments);
                String reactionsJson = new Gson().toJson(reactions);

                // Appeler la fonction JavaScript pour mettre à jour le graphique
                webView.getEngine().executeScript(
                    String.format("updateChart(%s, %s, %s, %s)",
                        labelsJson, postsJson, commentsJson, reactionsJson)
                );
            } catch (SQLException e) {
                LOGGER.severe("Erreur lors du chargement des statistiques : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static class StatData {
        private Date date;
        private int postsCount;
        private int commentsCount;
        private int reactionsCount;

        public StatData(Date date, int postsCount, int commentsCount, int reactionsCount) {
            this.date = date;
            this.postsCount = postsCount;
            this.commentsCount = commentsCount;
            this.reactionsCount = reactionsCount;
        }

        public Date getDate() {
            return date;
        }

        public int getPostsCount() {
            return postsCount;
        }

        public int getCommentsCount() {
            return commentsCount;
        }

        public int getReactionsCount() {
            return reactionsCount;
        }

        public int getYear() {
            return Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        }

        public int getMonth() {
            return Integer.parseInt(new SimpleDateFormat("MM").format(date));
        }
    }
}
