package org.gymify.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.gymify.entities.Equipe;
import org.gymify.entities.EquipeEvent;
import org.gymify.entities.Event;
import org.gymify.services.EquipeEventService;
import org.gymify.services.EventService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale; // Added for Locale.US
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller class for managing the list of participations (equipe-event associations) in the UI.
 */
public class ListeParticipationController {

    @FXML
    private Button btnRetour;

    @FXML
    private ScrollPane scrollPaneParticipations;

    @FXML
    private TextField searchField;

    @FXML
    private VBox vboxParticipations;

    @FXML
    private WebView weatherWebView;

    private final EquipeEventService equipeEventService = new EquipeEventService();
    private final EventService eventService = new EventService();
    private static final Logger LOGGER = Logger.getLogger(ListeParticipationController.class.getName());

    // Cache for weather data
    private String cachedWeatherHtml = null;
    private long lastWeatherFetchTime = 0;
    private static final long WEATHER_CACHE_DURATION = 60 * 60 * 1000; // 1 hour in milliseconds

    // Map for Open-Meteo weather codes to descriptions
    private static final Map<Integer, String> WEATHER_CODE_MAP = new HashMap<>();

    // Fixed coordinates for Tunis, Tunisia
    private static final double TUNIS_LATITUDE = 36.8065;
    private static final double TUNIS_LONGITUDE = 10.1815;

    static {
        // Populate weather code map (based on WMO codes used by Open-Meteo)
        WEATHER_CODE_MAP.put(0, "Clear sky");
        WEATHER_CODE_MAP.put(1, "Mainly clear");
        WEATHER_CODE_MAP.put(2, "Partly cloudy");
        WEATHER_CODE_MAP.put(3, "Overcast");
        WEATHER_CODE_MAP.put(45, "Fog");
        WEATHER_CODE_MAP.put(48, "Depositing rime fog");
        WEATHER_CODE_MAP.put(51, "Light drizzle");
        WEATHER_CODE_MAP.put(53, "Moderate drizzle");
        WEATHER_CODE_MAP.put(55, "Dense drizzle");
        WEATHER_CODE_MAP.put(61, "Slight rain");
        WEATHER_CODE_MAP.put(63, "Moderate rain");
        WEATHER_CODE_MAP.put(65, "Heavy rain");
        WEATHER_CODE_MAP.put(71, "Slight snow fall");
        WEATHER_CODE_MAP.put(73, "Moderate snow fall");
        WEATHER_CODE_MAP.put(75, "Heavy snow fall");
        WEATHER_CODE_MAP.put(95, "Thunderstorm");
        // Add more codes as needed
    }

    @FXML
    public void initialize() {
        loadParticipations("");

        // Add listener for search field to filter participations
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadParticipations(newValue);
        });

        // Load weather forecast
        loadWeatherForecast();
    }

    public void loadParticipations(String searchQuery) {
        if (vboxParticipations == null) {
            LOGGER.severe("vboxParticipations is null. Cannot load participations.");
            return;
        }

        vboxParticipations.getChildren().clear();
        try {
            List<EquipeEvent> participationList = equipeEventService.afficher();
            for (EquipeEvent participation : participationList) {
                Event event = eventService.recupererParId(participation.getIdEvent());
                if (event == null) continue;

                List<Equipe> equipes = equipeEventService.getEquipesForEvent(participation.getIdEvent());
                if (equipes.isEmpty()) continue;
                Equipe equipe = equipes.get(0); // Assuming one team per event

                boolean matchesSearch = searchQuery == null || searchQuery.trim().isEmpty() ||
                        (event.getNom() != null && event.getNom().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (event.getType() != null && event.getType().name().toLowerCase().contains(searchQuery.toLowerCase()));

                if (matchesSearch) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardParticipation.fxml"));
                    Parent card = loader.load();
                    CardParticipationController controller = loader.getController();
                    controller.setParticipation(event, equipe);
                    vboxParticipations.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des participations: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du chargement de la carte FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileMembre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du retour à l'interface ListeDesEvennements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadWeatherForecast() {
        try {
            long currentTime = System.currentTimeMillis();
            if (cachedWeatherHtml != null && (currentTime - lastWeatherFetchTime) < WEATHER_CACHE_DURATION) {
                weatherWebView.getEngine().loadContent(cachedWeatherHtml);
                LOGGER.info("Using cached weather data");
                return;
            }

            // Construct the Open-Meteo API URL for Tunis, using Locale.US to ensure dot as decimal separator
            String apiUrl = String.format(
                    Locale.US, // Use Locale.US to ensure dot (.) as decimal separator
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=temperature_2m_max,temperature_2m_min,weather_code&timezone=auto",
                    TUNIS_LATITUDE, TUNIS_LONGITUDE
            );

            LOGGER.info("Fetching weather data from: " + apiUrl);
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                LOGGER.severe("Failed to fetch weather data: HTTP error code " + responseCode + ", Response: " + errorResponse.toString());
                weatherWebView.getEngine().loadContent("<html><body><p>Erreur lors du chargement des données météo: " + errorResponse.toString() + "</p></body></html>");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.toString());
            JsonNode dailyNode = rootNode.get("daily");

            // Generate HTML table for the weather forecast
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>")
                    .append("<html>")
                    .append("<head>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; margin: 10px; }")
                    .append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                    .append("th, td { border: 1px solid #dcdcdc; padding: 8px; text-align: center; }")
                    .append("th { background-color: #2e86de; color: white; }")
                    .append("h3 { color: #333333; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h3>Météo à Tunis pour les 7 prochains jours</h3>")
                    .append("<table>")
                    .append("<tr><th>Date</th><th>Temp. Max (°C)</th><th>Temp. Min (°C)</th><th>Description</th></tr>");

            JsonNode timeNode = dailyNode.get("time");
            JsonNode tempMaxNode = dailyNode.get("temperature_2m_max");
            JsonNode tempMinNode = dailyNode.get("temperature_2m_min");
            JsonNode weatherCodeNode = dailyNode.get("weather_code");

            for (int i = 0; i < timeNode.size() && i < 7; i++) {
                String date = timeNode.get(i).asText();
                double tempMax = tempMaxNode.get(i).asDouble();
                double tempMin = tempMinNode.get(i).asDouble();
                int weatherCode = weatherCodeNode.get(i).asInt();
                String description = WEATHER_CODE_MAP.getOrDefault(weatherCode, "Unknown");

                html.append("<tr>")
                        .append("<td>").append(date).append("</td>")
                        .append("<td>").append(String.format("%.1f", tempMax)).append("</td>")
                        .append("<td>").append(String.format("%.1f", tempMin)).append("</td>")
                        .append("<td>").append(description).append("</td>")
                        .append("</tr>");
            }

            html.append("</table>")
                    .append("</body>")
                    .append("</html>");

            cachedWeatherHtml = html.toString();
            lastWeatherFetchTime = currentTime;
            weatherWebView.getEngine().loadContent(cachedWeatherHtml);
            LOGGER.info("Successfully loaded weather forecast into WebView.");

        } catch (IOException e) {
            LOGGER.severe("Erreur lors du chargement des données météo: " + e.getMessage());
            weatherWebView.getEngine().loadContent("<html><body><p>Erreur lors du chargement des données météo: " + e.getMessage() + "</p></body></html>");
            e.printStackTrace();
        }
    }
}