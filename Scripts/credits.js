const CreditContainer = document.getElementById('Credit-container'); 
const CreditTemplate = document.getElementById('Credit-template');

const loginButton = document.getElementById('in');
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;
const API_BASE = "http://localhost:8085/api";
const API_BAS3 = "http://localhost:8084/api";
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
      loadCredits()
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
 
function loadCredits() {
  CreditContainer.innerHTML = '';  

  fetch(`${API_BAS3}/clientCredit/getAll?direction=asc&page=0&size=120`, { 
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
    data.forEach(accounts => {
      const CreditBlock = CreditAccountTemplate.content.cloneNode(true);
      
      CreditBlock.querySelector('.name').textContent =  `Номер счёта: ${accounts.name}`;
      CreditBlock.querySelector('.balance').textContent = `Сумма на счету: ${accounts.balance} ₽.`;
      CreditBlock.querySelector('.createdDate').textContent =  `Дата создания счета: ${formatDate(accounts.createdDate)}.`;
      if(accounts.status == 'OPEN'){
       CreditBlock.querySelector('.status').textContent =  `Этот счет открыт.`;
      }else{
        CreditBlock.querySelector('.status').textContent =  `Этот счет закрыт.`;
        CreditBlock.querySelector('.deleteCredit').style.display = 'none';
      }
      CreditBlock.querySelector('.Credit-block').addEventListener('click', () => { 
        localStorage.setItem('selectedAccount', accounts.id); 
        window.location.href = '../pages/daccount.html'; 
      });
      CreditBlock.querySelector('.deleteCredit').addEventListener('click', () => { 
        wisdowcredit(accounts.id);
      });
      CreditContainer.appendChild(CreditBlock);     
    });
  })
  .catch(error => { 
    console.error('Ошибка получения списка дебетовых счетов:', error); 
    alert('Ошибка получения списка дебетовых счетов: ' + error);
  }); 
}