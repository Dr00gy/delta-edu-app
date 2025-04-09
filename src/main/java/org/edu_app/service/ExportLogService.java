package org.edu_app.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportLogService {

    private static final String LOG_FOLDER = "logs";
    private static final String EXPORT_LOG_FILE = "exports.log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Data
    public static class ExportRecord {
        private final LocalDateTime timestamp;
        private final Long userId;
        private final String exportType;
        private final String subject;
        private final String assignment;
    }

    /**
     * Gets the latest exports for a specific user
     * @param userId The user ID to filter by
     * @param limit Maximum number of records to return
     * @return List of export records, most recent first
     */
    public List<ExportRecord> getLatestExportsByUser(Long userId, int limit) {
        Path logFile = Paths.get(LOG_FOLDER, EXPORT_LOG_FILE);
        
        // If log file doesn't exist yet, return empty list
        if (!Files.exists(logFile)) {
            return Collections.emptyList();
        }
        
        try {
            List<String> lines = Files.readAllLines(logFile);
            List<ExportRecord> records = new ArrayList<>();
            
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    LocalDateTime timestamp = LocalDateTime.parse(parts[0], TIMESTAMP_FORMAT);
                    Long logUserId = Long.parseLong(parts[1]);
                    
                    // Filter by user ID
                    if (logUserId.equals(userId)) {
                        String exportType = parts[2];
                        String subject = parts[3];
                        String assignment = parts[4];
                        
                        records.add(new ExportRecord(timestamp, logUserId, exportType, subject, assignment));
                    }
                }
            }
            
            // Sort by timestamp descending (most recent first) and limit
            return records.stream()
                    .sorted((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()))
                    .limit(limit)
                    .collect(Collectors.toList());
            
        } catch (IOException e) {
            System.err.println("Error reading export log: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}