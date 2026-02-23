let flag= ""

window.addEventListener('load', () => {
  const authToken = localStorage.getItem('token');
  if (authToken) {
    console.log('Токен получен из localStorage:', localStorage.getItem('token'));
    //localStorage.getItem('email') проверка тухлости
    window.location.href = '../pages/main.html'
  }
});


function a(){ 
  const email = document.getElementById('email').value; 
  const password = document.getElementById('password').value; 

  if (email === '' || password === '') { 
    alert('Пожалуйста, заполните все поля!'); 
    return; 
  } 
  if (flag == "test"){
    localStorage.setItem('token', '123');
    localStorage.setItem('email', 'APIless');
    window.location.href = '../pages/main.html';
  }
  else{
    fetch('http://localhost:8080/auth/login', { 
      method: 'POST', 
      headers: { 
        'Content-Type': 'application/json' 
      }, 
      body: JSON.stringify({ username: email, password: password }) 
    }) 
    .then(response => { 
      if (!response.ok) { 
        
        return response.text().then(text => { throw new Error(text) }); 
      } 
      return response.json();
    }) 
    .then(data => { 
      const token = data.token; 

      if (token) {  
        localStorage.setItem('token', token);
        localStorage.setItem('email', email);
        window.location.href = '../pages/main.html'
      } else {  
        console.error('Токен не найден в заголовке ответа.');  
      }  
    }) 
    .catch(error => { 
      console.error('Ошибка входа:', error); 
      alert('Ошибка входа: ' + error.message); 
    });
  }
}