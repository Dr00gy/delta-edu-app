package org.edu_app.model.dto;

import lombok.Data;

@Data
public class SubmissionGradeDTO {
    private Long submissionId;
    private String studentName;
    private String uploadedFileName; // e.g. the filename of the uploaded file
    private String fileType;         // e.g. "image" or "text"
    private String fileContent;      // if text, the text; if image, a URL to the image
    private String studentComment;
}
