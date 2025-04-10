package org.edu_app.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ExportLogger {

    private static final String LOG_FOLDER = "logs";
    private static final String EXPORT_LOG_FILE = "exports.log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs an export event
     * @param userId The ID of the user who performed the export
     * @param exportType The type of export (files, grades, etc.)
     * @param subjectName The subject name if applicable, or null
     * @param assignmentName The assignment name if applicable, or null
     */
    public void logExport(Long userId, String exportType, String subjectName, String assignmentName) {
        try {
            Path logDir = Paths.get(LOG_FOLDER);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            // Create log entry
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String subject = subjectName != null ? subjectName : "All";
            String assignment = assignmentName != null ? assignmentName : "All";
            
            String logEntry = String.format("%s|%d|%s|%s|%s\n", 
                    timestamp, userId, exportType, subject, assignment);
            
            // Append to log file
            File logFile = new File(logDir.toFile(), EXPORT_LOG_FILE);
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(logEntry);
            }
        } catch (IOException e) {
            // Handle exception (should be logger)
            System.err.println("Error writing to export log: " + e.getMessage());
        }
    }
}