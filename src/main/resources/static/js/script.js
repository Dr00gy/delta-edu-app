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

    // Dark/light mode
    const modeToggle = document.querySelector(".mode-toggle");
    const modeIcon = document.getElementById("mode-icon");

    modeToggle.addEventListener("click", function () {
        document.body.classList.toggle("dark-mode");
        document.body.classList.toggle("light-mode");

        if (document.body.classList.contains("dark-mode")) {
            modeIcon.src = "/images/sun.svg";
        } else {
            modeIcon.src = "/images/moon.svg";
        }
    });
});
