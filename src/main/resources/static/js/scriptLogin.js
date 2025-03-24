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