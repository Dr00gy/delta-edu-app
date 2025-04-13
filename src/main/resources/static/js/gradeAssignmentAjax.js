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
            // If fileType indicates an image, display an image; otherwise, show text.
            if (data.fileType === 'image') {
                document.getElementById("gradeFileContent").innerHTML =
                    '<img src="' + data.fileContent + '" alt="Submitted image" style="max-width:200px;" />';
            } else {
                document.getElementById("gradeFileContent").textContent = data.fileContent;
            }
            document.getElementById("gradeStudentComment").textContent = data.studentComment;

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
            // Optionally, refresh the page or remove this submission from the table.
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
