const passwordField = document.getElementById('password');
const showPasswordButton = document.querySelector('.show-password');

if (showPasswordButton && passwordField) {
    showPasswordButton.addEventListener('click', function () {
        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            this.textContent = 'HIDE';
        } else {
            passwordField.type = 'password';
            this.textContent = 'SHOW';
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const usernameField = document.getElementById("username");
    const rememberMeCheckbox = document.getElementById("remember-me");
    
    if (localStorage.getItem("rememberedUsername")) {
        usernameField.value = localStorage.getItem("rememberedUsername");
        rememberMeCheckbox.checked = true;
    }

    document.querySelector(".login-form").addEventListener("submit", function () {
        if (rememberMeCheckbox.checked) {
            localStorage.setItem("rememberedUsername", usernameField.value);
        } else {
            localStorage.removeItem("rememberedUsername");
        }
    });
});
