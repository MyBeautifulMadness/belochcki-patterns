const DebitAccountContainer = document.getElementById('DebitAccount-container'); 
const DebitAccountTemplate = document.getElementById('DebitAccount-template');

const CreditAccountContainer = document.getElementById('CreditAccount-container'); 
const CreditAccountTemplate = document.getElementById('CreditAccount-template');

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
      loadDebitAccounts()
      loadCreditAccounts()
    } else {
      console.log('Токен невалиден');
      localStorage.clear();
    }
  } catch (e) {
    console.error('Ошибка при проверке токена', e);
    localStorage.clear();
    window.location.href = '../pages/login.html';
  }
});

function formatDate(dateString) { 
  const date = new Date(dateString); 
  const year = date.getFullYear(); 
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0'); 
 
  return `${year}-${month}-${day}`; 
} 
 
function loadDebitAccounts() {
  DebitAccountContainer.innerHTML = '';  

  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/debit-accounts`, { 
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
    //const DebitAccount = data.list;*/
    data.forEach(accounts => {
      const DebitAccountBlock = DebitAccountTemplate.content.cloneNode(true);
      
      DebitAccountBlock.querySelector('.name').textContent =  `Номер счёта: ${accounts.name}`;
      DebitAccountBlock.querySelector('.balance').textContent = `Сумма на счету: ${accounts.balance} ₽.`;
      DebitAccountBlock.querySelector('.createdDate').textContent =  `Дата создания счета: ${formatDate(accounts.createdDate)}.`;
      if(accounts.status == 'OPEN'){
        DebitAccountBlock.querySelector('.status').textContent =  `Этот счет открыт.`;
      }else{
        DebitAccountBlock.querySelector('.status').textContent =  `Этот счет закрыт.`;
        DebitAccountBlock.querySelector('.deleteDebitAccount').style.display = 'none';
      }
      DebitAccountBlock.querySelector('.DebitAccount-block').addEventListener('click', () => { 
        localStorage.setItem('selectedAccount', accounts.id); 
        window.location.href = '../pages/daccount.html'; 
      });
      DebitAccountBlock.querySelector('.deleteDebitAccount').addEventListener('click', () => { 
        deleteDebitAccount(accounts.id);
      });
      DebitAccountContainer.appendChild(DebitAccountBlock);     
    });
  })
  .catch(error => { 
    console.error('Ошибка получения списка дебетовых счетов:', error); 
    alert('Ошибка получения списка дебетовых счетов: ' + error);
  }); 
}

function loadCreditAccounts() {
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



function creatDebitAccount() {
  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/debit-accounts`, { 
    method: 'POST', 
    headers: { 
      Authorization: `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json' 
    },      
  })
  .then(response => { 
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text) }); 
    } 
    loadDebitAccounts()
    return response.json();
  }) 
  .catch(error => { 
    console.error('Ошибка создания дебетого счёта:', error); 
    alert('Ошибка создания дебетого счёта: ' + error);
  }); 
}

function deleteDebitAccount(id) {
  fetch(`${API_BASE}/gateway/accounts/clients/${ClientID}/debit-accounts/${id}/close`, { 
    method: 'POST', 
    headers: { 
      Authorization: `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json' 
    },      
  })
  .then(response => { 
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text) }); 
    } 
    loadDebitAccounts()
    return response.json();
  }) 
  .catch(error => { 
    console.error('Ошибка закрытия дебетого счёта:', error); 
    alert('Ошибка закрытия дебетого счёта: ' + error);
  }); 
}