package org.edu_app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple static utility class to maintain a registry of file paths
 * The registry is saved to disk as JSON in uploads/log.txt
 */
public class FileRegistry {
    private static final String LOG_FILE_PATH = "uploads/log.txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Main storage - maps keys to lists of file paths
    private static Map<String, List<String>> filePathMap = new HashMap<>();

    // Load the registry from disk
    public static void load() {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            // If file doesn't exist, create empty map
            filePathMap = new HashMap<>();
            // Create the directory if needed
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
        } else {
            try {
                // Simple TypeReference for deserialization
                filePathMap = objectMapper.readValue(logFile,
                        new TypeReference<HashMap<String, List<String>>>() {});
            } catch (IOException e) {
                System.err.println("Error loading file registry: " + e.getMessage());
                filePathMap = new HashMap<>();
            }
        }
    }

    // Save the registry to disk
    public static void save() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            objectMapper.writeValue(logFile, filePathMap);
        } catch (IOException e) {
            System.err.println("Error saving file registry: " + e.getMessage());
        }
    }

    // Register a file path with a key
    public static void registerFile(String key, String filePath) {
        if (!filePathMap.containsKey(key)) {
            filePathMap.put(key, new ArrayList<>());
        }
        filePathMap.get(key).add(filePath);
        save(); // Save after each change
    }

    // Get all file paths for a key
    public static List<String> getFilePaths(String key) {
        return filePathMap.getOrDefault(key, new ArrayList<>());
    }

    // Get all entries in the registry
    public static Map<String, List<String>> getAllFilePaths() {
        return new HashMap<>(filePathMap);
    }

    // Get file content as byte array
    public static byte[] getFileContent(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    // Clear all entries for a key
    public static void clearEntries(String key) {
        filePathMap.remove(key);
        save();
    }

    // Clear all registry entries
    public static void clearAll() {
        filePathMap.clear();
        save();
    }
}