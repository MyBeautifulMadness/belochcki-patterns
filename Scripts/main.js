const loginButton = document.getElementById('in');
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;

function activate() {
  if (!userMenuListenerAttached) {
    loginButton.addEventListener('click', () => {
      window.location.href = '../pages/login.html'
    });

    loginButton.textContent = 'Выход';
    loginButton.style.color = "rgb(255, 255, 255)"
    profileButton.style.display = 'inline-block';
    logoutButton.style.display = 'inline-block';

    profileButton.addEventListener('click', () => {
      window.location.href = '../pages/profile.html'
    });

    logoutButton.addEventListener('click', () => {
      localStorage.removeItem('token');
      window.location.href = '../pages/login.html'
    });

    document.addEventListener('click', (event) => {
      const target = event.target;
      if (userMenu.style.display === 'block' &&
          target !== userMenu &&
          !userMenu.contains(target) &&
          target !== loginButton) {
        userMenu.style.display = 'none';
      }
    });

    userMenuListenerAttached = true;
  }
}
window.addEventListener('load', () => {
    const authToken = localStorage.getItem('token');
    if (authToken) {
      console.log('Токен получен из localStorage:', localStorage.getItem('token'));
      activate();
    } else {
      console.log('Токен не найден в localStorage.');
      window.location.href = '../pages/login.html'
    }
});