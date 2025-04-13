// Function to toggle the create assignment modal.
function toggleCreateAssignmentModal() {
    const modal = document.getElementById("createAssignmentModal");
    modal.style.display = (modal.style.display === "block") ? "none" : "block";
}

// Open the create assignment modal.
function openCreateAssignmentModal() {
    // If you want to pre-fill any fields or do extra logic, you can do it here.
    toggleCreateAssignmentModal();
}

// Called when the Confirm button in the create modal is clicked.
function confirmCreateAssignment() {
    // Retrieve values from the createAssignmentForm.
    const name = document.getElementById("newAssignmentName").value;
    const description = document.getElementById("newAssignmentDescription").value;
    const maxPoints = document.getElementById("newMaxPoints").value;
    const deadline = document.getElementById("newDeadline").value;

    // Retrieve the subjectId.
    // You can pass the subjectId from the subjectDetails page.
    // For example, store it in a data attribute on the body or another element.
    const subjectId = document.getElementById("subjectIdHolder").getAttribute("data-subject-id");
    console.log("Creating assignment for subject:", subjectId);

    console.log("Creating assignment:", name, description, maxPoints, deadline, "for subject", subjectId);

    // Retrieve CSRF token and header from meta tags (if using Spring Security).
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    fetch('/api/assignments/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            name: name,
            description: description,
            maxPoints: Number(maxPoints),
            deadline: deadline, // This should be in ISO format; datetime-local input returns an ISO-compatible string.
            subjectId: Number(subjectId)
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error creating assignment");
            }
            return response.text(); // assume no JSON output
        })
        .then(result => {
            console.log("Assignment creation successful:", result);
            toggleCreateAssignmentModal(); // Hide the modal on success.
            // Optionally, refresh the assignment list on the page.
            location.reload();
        })
        .catch(error => {
            console.error("Error in assignment creation fetch:", error);
            // Optionally alert the error for the user.
            toggleCreateAssignmentModal();
        });
}

// Optional: close the create modal if clicking outside it.
window.onclick = function(event) {
    const modal = document.getElementById("createAssignmentModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
};
