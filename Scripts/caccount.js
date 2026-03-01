const CreditAccountContainer = document.getElementById('CreditAccount-container'); 
const CreditAccountTemplate = document.getElementById('CreditAccount-template');
const OperstionsContainer = document.getElementById('Operstions-container'); 
const OperstionsTemplate = document.getElementById('Operstions-template');

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

window.addEventListener('load', async() => {
  const token = localStorage.getItem('token');
  
  if (!token) window.location.href = '../pages/login.html';
  try {
    const res = await fetch(`${API_BASE}/auth/validate`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token })
    });
    if (res.ok) {
      console.log('Токен валиден');
      
      activate();
      loadCreditAccount() 
      loadOperstions()
    } else {
      console.log('Токен невалиден');
      localStorage.clear();
    }
  } catch (e) {
    console.error('Ошибка при проверке токена', e);
    localStorage.clear();
    alert('1');
  }
});

function formatDate(dateString) { 
  const date = new Date(dateString); 
  const year = date.getFullYear(); 
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0'); 
 
  return `${year}-${month}-${day}`; 
} 
 
function loadCreditAccount() {
  CreditAccountContainer.innerHTML = '';

  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/credit-account`, { 
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
    console.log(data);
    const CreditAccountBlock = CreditAccountTemplate.content.cloneNode(true);
    
    CreditAccountBlock.querySelector('.name').textContent =  `Номер счёта: ${data.name}`;
    CreditAccountBlock.querySelector('.balance').textContent = `Сумма на счету: ${data.balance} ₽.`;
    CreditAccountBlock.querySelector('.createdDate').textContent =  `Дата создания счета: ${formatDate(data.createdDate)}.`;
    if(data.status == 'OPEN'){
      CreditAccountBlock.querySelector('.status').textContent =  `Этот счет открыт.`;
    }else{
      CreditAccountBlock.querySelector('.status').textContent =  `Этот счет закрыт.`;
    }
    CreditAccountBlock.querySelector('.CreditAccount-block').addEventListener('click', () => { 
      localStorage.setItem('selectedAccount', data.id); 
      window.location.href = '../pages/caccount.html'; 
    });
    CreditAccountContainer.appendChild(CreditAccountBlock);     
  })
  .catch(error => { 
    console.error('Ошибка получения информации о кредитном счёте:', error); 
    alert('Ошибка получения информации о кредитном счёте: ' + error);
  }); 
}


function loadOperstions() {
  OperstionsContainer.innerHTML = '';  
  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/accounts/${localStorage.getItem('selectedAccount')}/operations?accountType=CREDIT&page=0&size=120&sort=ASC`, { 
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
    console.log(data);
    const content = data.content;
    content.forEach(Operstion => {
      const OperstionsBlock = OperstionsTemplate.content.cloneNode(true);
      OperstionsBlock.querySelector('.comment').textContent =  `Результат: ${Operstion.comment}`;
      OperstionsBlock.querySelector('.operationType').textContent =  `Тип операции: ${Operstion.operationType}`;
      OperstionsBlock.querySelector('.time').textContent =  `Время операции: ${Operstion.time}`;
      OperstionsBlock.querySelector('.amount').textContent = `Сумма перевода: ${Operstion.amount} ₽.`;
      OperstionsBlock.querySelector('.date').textContent =  `Дата операции: ${formatDate(Operstion.date)}.`;
      if(Operstion.operationType == 'OPEN' || Operstion.operationType == 'CLOSE'){
        OperstionsBlock.querySelector('.amount').style.display = 'none';
      }
      OperstionsContainer.appendChild(OperstionsBlock);  
    })   
  })
  .catch(error => { 
    console.error('Ошибка получения списка операций дебетового счета:', error); 
    alert('Ошибка получения списка операций дебетового счета: ' + error);
  }); 
}

function withdraw(){
  if (document.getElementById('amount').value === '') { 
    alert('Пожалуйста, укажите сумму операции!'); 
    return; 
  } 
  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/credit-accounts/${localStorage.getItem('selectedAccount')}/withdraw`, { 
    method: 'POST', 
    headers: { 
      Authorization: `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json' 
    },      
    body: JSON.stringify({
      amount: document.getElementById('amount').value,
      comment: "Мы отберём у вас все деньги"
    })
  })
  .then(response => { 
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text) }); 
    } 
    loadCreditAccount() 
    loadOperstions();
    return response.json();
  }) 
  .catch(error => { 
    console.error('Ошибка списания с кредитного счета:', error); 
    alert('Ошибка списания с кредитного счета: ' + error);
  }); 
}