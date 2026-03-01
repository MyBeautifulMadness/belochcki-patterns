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
      loadDebitAccounts()
      loadCreditAccounts()
    } else {
      alert("Необходимо войти в аккаунт");
      window.location.href = '../pages/login.html'
    }
});

function formatDate(dateString) { 
  const date = new Date(dateString); 
  const year = date.getFullYear(); 
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0'); 
 
  return `${year}-${month}-${day}`; 
} 

function asArray(data) {
  if (Array.isArray(data)) return data;
  if (data && Array.isArray(data.content)) return data.content; // Spring Page
  return [];
} 

function loadDebitAccounts() {
  DebitAccountContainer.innerHTML = '';  

  fetch(`${API_BASE}/gateway/accounts/debit-accounts`, { 
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
    console.log('debit raw:', data);
    //const DebitAccount = data.list;*/

    const list = asArray(data);
    console.log('debit list:', list);

    if (list.length === 0) {
      return;
    }

    list.forEach(accounts => {
      const DebitAccountBlock = DebitAccountTemplate.content.cloneNode(true);
      
      DebitAccountBlock.querySelector('.name').textContent =  `Номер счёта: ${accounts.name}`;
      DebitAccountBlock.querySelector('.balance').textContent = `Сумма на счету: ${accounts.balance} ₽.`;
      DebitAccountBlock.querySelector('.createdDate').textContent =  `Дата создания счета: ${formatDate(accounts.createdDate)}.`;
      if(accounts.status == 'OPEN'){
        DebitAccountBlock.querySelector('.status').textContent =  `Этот счет открыт.`;
      }else{
        DebitAccountBlock.querySelector('.status').textContent =  `Этот счет закрыт.`;
      }
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

  fetch(`${API_BASE}/gateway/accounts/credit-accounts`, { 
    method: 'GET', 
    headers: { 
      Authorization: `Bearer ${localStorage.getItem('token')}`  
    },      
  })
  .then(response => { 
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text) }); 
    } 
    return response.json();
  }) 
  .then(data => {
    console.log('credit raw:', data);

    const list = asArray(data);
    console.log('credit list:', list);

    if (list.length === 0) {
      return;
    }


    const content = data.content;
    content.forEach(data => {
        const CreditAccountBlock = CreditAccountTemplate.content.cloneNode(true);

        CreditAccountBlock.querySelector('.name').textContent =  `Номер счёта: ${data.name}`;
        CreditAccountBlock.querySelector('.balance').textContent = `Сумма на счету: ${data.balance} ₽.`;
        CreditAccountBlock.querySelector('.createdDate').textContent =  `Дата создания счета: ${formatDate(data.createdDate)}.`;
        if(data.status == 'OPEN'){
        CreditAccountBlock.querySelector('.status').textContent =  `Этот счет открыт.`;
        }else{
        CreditAccountBlock.querySelector('.status').textContent =  `Этот счет закрыт.`;
        }

        CreditAccountContainer.appendChild(CreditAccountBlock);
    })     
  })
  .catch(error => { 
    console.error('Ошибка получения информации о кредитном счёте:', error); 
    alert('Ошибка получения информации о кредитном счёте: ' + error);
  }); 
}