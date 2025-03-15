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

    // Close menu when clicking outside of it
    //document.addEventListener("click", function (event) {
        //if (!menu.contains(event.target) && !burger.contains(event.target)) {
            //menu.classList.remove("show");
        //}
    //});
});
