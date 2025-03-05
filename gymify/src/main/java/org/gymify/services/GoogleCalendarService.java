package org.gymify.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "GYMIFY";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    private static Credential getCredentials() throws Exception {
        // Load credentials from the credentials.json file
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(GoogleCalendarService.class.getResourceAsStream("/client_secret_426973845068-i5dlbsneuv5a2ctqig2aotfai3f33kll.apps.googleusercontent.com.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Calendar getCalendarService() throws Exception {
        Credential credential = getCredentials();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Method to fetch Google Calendar events
    public static List<Event> getGoogleCalendarEvents() throws Exception {
        Calendar service = getCalendarService();

        // Fetch events from the primary calendar
        Events events = service.events().list("primary")
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
}