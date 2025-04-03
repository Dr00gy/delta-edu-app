function displayFileNames() {
    const fileInput = document.getElementById("file-upload");
    const fileNameDisplay = document.getElementById("file-names");

    if (fileInput.files.length > 0) {
        let fileNames = [];
        for (let i = 0; i < fileInput.files.length; i++) {
            fileNames.push(fileInput.files[i].name);
        }
        fileNameDisplay.textContent = fileNames.join(", ");
    } else {
        fileNameDisplay.textContent = "No files chosen!";
    }
}
