package org.gymify.entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeoCoding {
    private static final Map<String, double[]> locationCache = new HashMap<>();

    public static double[] getCoordinatesFromAPI(String location) {
        // Vérifier si le lieu est déjà dans le cache
        if (locationCache.containsKey(location)) {
            return locationCache.get(location);
        }

        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" +
                    location.replace(" ", "%20") + "&format=json&limit=1";

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject json = jsonArray.getJSONObject(0);
                double lat = json.getDouble("lat");
                double lon = json.getDouble("lon");
                double[] coordinates = new double[]{lat, lon};
                // Ajouter au cache
                locationCache.put(location, coordinates);
                return coordinates;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}