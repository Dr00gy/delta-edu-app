document.addEventListener("DOMContentLoaded", function () {
    const menu = document.getElementById("menu");
    const burger = document.querySelector(".burger");
    const closeBtn = document.getElementById("close-menu");

    // Toggle menu via burger
    burger.addEventListener("click", function () {
        menu.classList.toggle("show");
    });

    // Close menu w/ arrow
    closeBtn.addEventListener("click", function () {
        menu.classList.remove("show");
    });

    // Closes by clicking outside of the menu
    //document.addEventListener("click", function (event) {
        //if (!menu.contains(event.target) && !burger.contains(event.target)) {
            //menu.classList.remove("show");
        //}
    //});

    // Dark/light mode
    const modeToggle = document.querySelector(".mode-toggle");

    modeToggle.addEventListener("click", function () {
        document.body.classList.toggle("light-mode");
        document.body.classList.toggle("dark-mode");
    });
});

  document.addEventListener("DOMContentLoaded", function () {
      document.querySelectorAll(".custom-table tbody tr").forEach(row => {
          let status = row.getAttribute("data-status");

          if (status === "excellent") {
              row.style.backgroundColor = "#c8e6c9"; // Light green
          } else if (status === "average") {
              row.style.backgroundColor = "#fff9c4"; // Light yellow
          } else if (status === "poor") {
              row.style.backgroundColor = "#ffccbc"; // Light red
          }
      });
  });

