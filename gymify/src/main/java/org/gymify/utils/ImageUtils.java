package org.gymify.utils;

import javafx.scene.image.Image;

import java.io.File;

public class ImageUtils {
    public static Image loadImage(String url, String defaultImagePath) {
        if (url != null && !url.trim().isEmpty()) {
            try {
                // Check if the URL is an absolute file path
                File file = new File(url);
                if (file.exists()) {
                    return new Image(file.toURI().toString(), true);
                }

                // Check if it's a resource path (starts with /)
                if (url.startsWith("/")) {
                    var resource = ImageUtils.class.getResource(url);
                    if (resource != null) {
                        return new Image(resource.toExternalForm(), true);
                    } else {
                        System.err.println("Resource not found: " + url);
                    }
                }

                // Try loading as an external URL or file
                return new Image(url, true);
            } catch (Exception e) {
                System.err.println("Failed to load image: " + url + ", using default image.");
            }
        }
        // Load default image from resources
        var defaultResource = ImageUtils.class.getResource(defaultImagePath);
        if (defaultResource != null) {
            return new Image(defaultResource.toExternalForm(), true);
        } else {
            System.err.println("Default image not found: " + defaultImagePath);
            return new Image("file:src/main/resources/images/default-image.png", true); // Fallback to a file-based default
        }
    }
}