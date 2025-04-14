// Global variable to hold the submission id currently being graded.
var currentSubmissionIdForGrading = null;

function toggleGradeModal() {
    const modal = document.getElementById("gradeModal");
    modal.style.display = (modal.style.display === "block") ? "none" : "block";
}

function openGradeModal(btn) {
    // Get submission id from the button's data attribute.
    const submissionId = btn.getAttribute("data-submission-id");
    currentSubmissionIdForGrading = submissionId;
    console.log("Opening grade modal for submission:", submissionId);

    // Fetch submission details via AJAX GET.
    fetch('/api/grades/' + submissionId + '/details')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error loading submission details");
            }
            return response.json();
        })
        .then(data => {
            console.log("Submission details:", data);
            // Populate the modal fields.
            document.getElementById("gradeStudentName").textContent = data.studentName;
            document.getElementById("gradeUploadedFile").textContent = data.uploadedFileName;
            
            const fileContentElement = document.getElementById("gradeFileContent");
            fileContentElement.innerHTML = ''; // Clear previous content
            
            // Display file content based on file type
            if (data.fileType === 'image') {
                // For images, create an img element with the base64 data
                const img = document.createElement('img');
                img.src = data.fileContent; // This should be a base64 data URL
                img.alt = "Submitted image";
                img.style.maxWidth = "100%";
                img.style.borderRadius = "4px";
                img.style.boxShadow = "0 1px 3px rgba(0,0,0,0.1)";
                fileContentElement.appendChild(img);
            } else if (data.fileType === 'text') {
                // For text files, create a pre element for formatted display
                const pre = document.createElement('pre');
                pre.style.whiteSpace = 'pre-wrap';
                pre.style.maxHeight = '300px';
                pre.style.overflow = 'auto';
                pre.style.padding = '10px';
                pre.style.backgroundColor = '#f5f5f5';
                pre.style.border = '1px solid #ddd';
                pre.style.borderRadius = '4px';
                pre.style.fontSize = '14px';
                pre.textContent = data.fileContent;
                fileContentElement.appendChild(pre);
            } else {
                // For other file types or if no file
                fileContentElement.textContent = data.fileContent || "No file content available";
            }
            
            document.getElementById("gradeStudentComment").textContent = data.studentComment || "No comment provided";

            // Clear previous inputs.
            document.getElementById("gradeScore").value = "";
            document.getElementById("teacherFeedback").value = "";

            // Show the modal.
            toggleGradeModal();
        })
        .catch(error => {
            console.error("Error loading submission details:", error);
        });
}

function confirmGrade() {
    const gradeScore = document.getElementById("gradeScore").value;
    const teacherFeedback = document.getElementById("teacherFeedback").value;

    if (!currentSubmissionIdForGrading) {
        console.error("No submission id stored for grading");
        return;
    }

    // Retrieve CSRF token and header.
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    fetch('/api/grades/updateGrade', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            submissionId: currentSubmissionIdForGrading,
            grade: Number(gradeScore),
            teacherFeedback: teacherFeedback
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error updating grade");
            }
            return response.text();
        })
        .then(result => {
            console.log("Grade update successful:", result);
            toggleGradeModal();
            // Redirect to the grading page after successful update
            window.location.href = "/grading";
        })
        .catch(error => {
            console.error("Error in grade update fetch:", error);
            toggleGradeModal();
        });
}

// Optional: close the grade modal if clicking outside its content.
window.onclick = function(event) {
    const gradeModal = document.getElementById("gradeModal");
    if (event.target === gradeModal) {
        gradeModal.style.display = "none";
    }
};