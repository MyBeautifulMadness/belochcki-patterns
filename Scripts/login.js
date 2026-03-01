window.addEventListener('load', async() => {
  const token = localStorage.getItem('token');
  if (!token) return;
  try {
    const res = await fetch('http://localhost:8085/api/auth/validate', {
      method: 'POST',
      headers: {
        'Authorization': token
      }
    });

    if (res.ok) {
      console.log('Токен валиден, редиректим на main.html');
      window.location.href = '../pages/main.html';
    } else {
      console.log('Токен невалиден');
      localStorage.clear();
    }
  } catch (e) {
    console.error('Ошибка при проверке токена', e);
    localStorage.clear();
  }
});


function a(){ 
  const email = document.getElementById('email').value; 
  const password = document.getElementById('password').value; 

  if (email === '' || password === '') { 
    alert('Пожалуйста, заполните все поля!'); 
    return; 
  } 
  else{
    fetch('http://localhost:8085/api/auth/login', { 
      method: 'POST', 
      headers: { 
        'Content-Type': 'application/json' 
      }, 
      body: JSON.stringify({
        login: email,
        password: password,
        userType: "CLIENT"
      })
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
        localStorage.setItem('userId', data.userId);
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