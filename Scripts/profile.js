const username = document.getElementById('username');
const birthdate = document.getElementById('birthdate');
const useremail = document.getElementById('email');
const firstName = document.getElementById('firstName');
const lastName = document.getElementById('lastName');
const phone = document.getElementById('phone');

const savebutton = document.getElementById('save');
const loginButton = document.getElementById('in');
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;
const API_BASE = "http://localhost:8085/api";
const ClientID = localStorage.getItem('userId');

function activate(){
  if (!userMenuListenerAttached) {
    loginButton.addEventListener('click', () => {
    userMenu.style.display = userMenu.style.display === 'block' ? 'none' : 'block';
    });
    loginButton.textContent = 'Возможности ▾';
    profileButton.style.display = 'inline-block';
    logoutButton.style.display = 'inline-block';

    profileButton.addEventListener('click', () => {
      window.location.href = '../pages/profile.html'
    });

    logoutButton.addEventListener('click', () => {
      fetch(`${API_BASE}/auth/logout`, {  
        method: 'POST',  
        headers: {  
          Authorization: `Bearer ${localStorage.getItem('token')}`
        },  
      })  
      .catch(error => {  
        console.error('Ошибка выхода из профиля:', error);  
        alert('Ошибка выхода из профиля: ' + error.message);  
      });  
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

function formatDate(dateString) { 
  const date = new Date(dateString); 
  const year = date.getFullYear(); 
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0'); 
 
  return `${year}-${month}-${day}`; 
} 
 
window.addEventListener('load', () => { 
  const authToken = localStorage.getItem('token'); 
  if (authToken) { 
    email=localStorage.getItem('email') 
    document.getElementById('in').textContent=email; 
    activate(email); 
    return
    fetch('http://localhost:8080/auth/profile', {  
      method: 'GET',  
      headers: {  
        Authorization: `Bearer ${localStorage.getItem('token')}`, 
        'Content-Type': 'application/json'  
      },  
    })  
    .then(response => {  
      if (!response.ok) {  
          
        return response.text().then(text => { throw new Error(text) });  
      }  
      return response.json(); 
    }) 
    .then(data => { 
      username.value = data.username || '';
      birthdate.value = formatDate(data.birthday) || '';
      useremail.value = data.email || '';
      firstName.value = data.firstName || '';
      lastName.value = data.lastName || '';
      phone.value = data.phone || '';
      console.log(data.phone);
      savebutton.addEventListener('click',()=>{ 
        fetch('http://localhost:8080/auth/profile', {  
          method: 'PUT',  
          headers: {  
            Authorization: `Bearer ${localStorage.getItem('token')}`, 
            'Content-Type': 'application/json' 
          },  
          body: JSON.stringify({ birthday: birthdate.value, email: useremail.value, firstName: firstName.value, lastName: lastName.value, phone: phone.value})  
        })  
        .then(response => {  
          
          if (!response.ok) {  
            return response.text().then(text => { throw new Error(text) });  
          } 
        })  
      }); 
    })  
    .catch(error => {  
      console.error('Ошибка получения параметров профиля:', error);  
      alert('Ошибка получения параметров профиля: ' + error.message);  
    });  
  } else { 
    console.log('Токен не найден в localStorage.'); 
    loginButton.addEventListener('click', () => { 
      window.location.href = '../pages/login.html'; 
    }); 
  } 
}); 
$(function() {  
    $( "#birthdate" ).datepicker({  
      dateFormat: "yy-mm-dd", 
      changeMonth: true,  
    changeYear: true,  
      yearRange: "1900:2024", 
    });  
  }
);  
 
$(function() {  
  $("#phone").keyup(function() {  
    let phone = $(this).val().replace(/\D/g, "");  
    let formattedPhone = "";  
    phone = phone.substring(0, 12); 
      
    if (phone.length > 0) {  
      formattedPhone += "+7 (";  
    } 
    if (phone.length >= 4) {  
      formattedPhone += phone.substring(1, 4) + ") ";  
    } else {  
      formattedPhone += phone.substring(1, phone.length);  
    }  
    if (phone.length >= 7) {  
      formattedPhone += phone.substring(4, 7) + "-";  
    } else if (phone.length > 4) {  
        formattedPhone += phone.substring(4, phone.length);  
    }  
    if (phone.length >= 9) {  
      formattedPhone += phone.substring(7, 9) + "-";  
    } else if (phone.length > 7) {
      formattedPhone += phone.substring(7, phone.length) ;  
    }  
    if (phone.length >= 11) {  
      formattedPhone += phone.substring(9, 11);  
    } else if (phone.length > 9) {  
        formattedPhone += phone.substring(9, phone.length);  
    }  
    $(this).val(formattedPhone);  
  });  
}); 