console.log("Current role from global variable: " + currentRole);

    // Toggle modal display function.

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("settingsModal");
    if (modal) {
        // Now it's safe to access modal.style.
        modal.style.display = "none";
    }
});

function toggleSettings() {
    const modal = document.getElementById("settingsModal");
    modal.style.display = (modal.style.display === "block") ? "none" : "block";
}

// Called when a settings button is clicked.
function openSettingsModal(btn) {
    // Retrieve data attributes.
    const submissionId = btn.getAttribute("data-submission-id");
    const studentComment = btn.getAttribute("data-student-comment");
    const assignmentId = btn.getAttribute("data-assignment-id");
    const assignmentName = btn.getAttribute("data-assignment-name");
    const assignmentDescription = btn.getAttribute("data-assignment-description");
    const maxPoints = btn.getAttribute("data-max-points");
    const deadline = btn.getAttribute("data-deadline");
    console.log("Assignment ID:", assignmentId);
    console.log("Assignment Name:", assignmentName);
    console.log("Assignment Description:", assignmentDescription);
    console.log("Points:", maxPoints);
    console.log("Deadline:", deadline);

    if (currentRole === 'STUDENT') {
        const commentField = document.getElementById("studentComment");
        if (commentField) {
            commentField.value = studentComment;
        }
        // Save the submission ID on the modal for later use (when Confirm is clicked).
        document.getElementById("settingsModal").setAttribute("data-submission-id", submissionId);
    } else if (currentRole === 'TEACHER') { // THIS DIDNT TRIGGER
        // Populate teacher fields if necessary.
        const nameField = document.getElementById("assignmentName");
        const descField = document.getElementById("assignmentDescription");
        const pointsField = document.getElementById("maxPoints");
        const deadlineField = document.getElementById("deadline");

        if (nameField) nameField.value = assignmentName || "";
        if (descField) descField.value = assignmentDescription || "";
        if (pointsField) pointsField.value = maxPoints || "";
        if (deadlineField) deadlineField.value = deadline || "";
        console.log("Opening modal for assignment:");
        console.log("Assignment ID:", assignmentId);
        console.log("Assignment Name:", assignmentName);
        console.log("Assignment Description:", assignmentDescription);
        console.log("Points:", maxPoints);
        console.log("Deadline:", deadline);
        // Save the assignment ID on the modal so we can use it when updating.
        document.getElementById("settingsModal").setAttribute("data-assignment-id", assignmentId);
    }
    toggleSettings();
}

// Called when the Confirm button in the modal is clicked.
function confirmAction() {
    console.log("confirmAction() invoked");
    if (currentRole === 'STUDENT') {
        const updatedComment = document.getElementById("studentComment").value;
        const submissionId = document.getElementById("settingsModal").getAttribute("data-submission-id");
        console.log("Updated student comment:", updatedComment);

        // Retrieve CSRF token and header name from meta tags
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        fetch('/api/submissions/updateComment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken  // add the CSRF token header
            },
            body: JSON.stringify({
                submissionId: submissionId,
                studentComment: updatedComment
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error updating comment");
                }
                return response.text();  // assume no JSON body returned
            })
            .then(result => {
                console.log("Update successful", result);
                toggleSettings();  // Hide the modal
            })
            .catch(error => {
                console.error("Error:", error);
                toggleSettings();
            });
    } else if (currentRole === 'TEACHER') {
        // For teacher, gather updated assignment info.
        console.log("Teacher branch entered in confirmAction()");
        console.log("Assignment Name fieldCONFIRMATION:", document.getElementById("assignmentName"));
        console.log("Assignment Description field:", document.getElementById("assignmentDescription"));
        const updatedName = document.getElementById("assignmentName").value;
        const updatedDescription = document.getElementById("assignmentDescription").value;
        const updatedMaxPoints = document.getElementById("maxPoints").value;
        const updatedDeadline = document.getElementById("deadline").value;
        const assignmentId = document.getElementById("settingsModal").getAttribute("data-assignment-id");

        console.log("Updated assignment details:", updatedName, updatedDescription, updatedMaxPoints, updatedDeadline);

        // Retrieve CSRF token and header if your application uses Spring Security.
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        fetch('/api/assignments/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                assignmentId: assignmentId,
                name: updatedName,
                description: updatedDescription,
                maxPoints: Number(updatedMaxPoints),
                deadline: updatedDeadline  // pass an ISO string; Jackson will convert it.
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error updating assignment");
                }
                return response.text();
            })
            .then(result => {
                console.log("Assignment update successful", result);
                toggleSettings();  // Hide the modal on success.
            })
            .catch(error => {
                console.error("Error:", error);
                toggleSettings();
            });
    }
}

// Close the modal if clicking outside its content.
window.onclick = function(event) {
    const modal = document.getElementById("settingsModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
};
