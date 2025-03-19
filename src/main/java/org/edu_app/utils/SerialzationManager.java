package org.edu_app.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerialzationManager {
    @Getter
    static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    static final String SAVE_PATH = "saves";

    // serialization utils
    public static String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
    public static <T> T deserialize(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    // file managing utils
    public static <T> void save(T obj, String relativePath) throws IOException {
        // Create full path
        Path fullPath = Paths.get(SAVE_PATH, relativePath);

        // Create parent directories recursively
        Files.createDirectories(fullPath.getParent());

        // Serialize and save the object
        String json = serialize(obj);
        Files.writeString(fullPath, json);
    }


    public static <T> T load(String relativePath, Class<T> clazz) throws IOException {
        // Create full path
        Path fullPath = Paths.get(SAVE_PATH, relativePath);

        // Check if file exists
        if (!Files.exists(fullPath)) {
            throw new IOException("File not found: " + fullPath);
        }

        // Read and deserialize the object
        String json = Files.readString(fullPath);
        return deserialize(json, clazz);
    }
}
