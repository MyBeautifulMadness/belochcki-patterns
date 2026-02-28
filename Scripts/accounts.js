const DebitAccountContainer = document.getElementById('DebitAccount-container'); 
const DebitAccountTemplate = document.getElementById('DebitAccount-template');

const CreditAccountContainer = document.getElementById('CreditAccount-container'); 
const CreditAccountTemplate = document.getElementById('CreditAccount-template');



const loginButton = document.getElementById('in');
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;

let currentPage = 1; 
let pageSize = 5; 
let pagecount;
let currentFilters = {}; 
let currentSorting = ''; 

function activate(email){
  if (!userMenuListenerAttached) {
    loginButton.addEventListener('click', () => {
    userMenu.style.display = userMenu.style.display === 'block' ? 'none' : 'block';
    });
    loginButton.textContent = email + ' ▾';
    profileButton.style.display = 'inline-block';
    logoutButton.style.display = 'inline-block';

    profileButton.addEventListener('click', () => {
      window.location.href = '../pages/profile.html'
    });

    logoutButton.addEventListener('click', () => {
      fetch('http://localhost:8080/auth/logout', {  
        method: 'DELETE',  
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
    userMenuListenerAttached = true;
  }
}

window.addEventListener('load', () => {
    const authToken = localStorage.getItem('token');
    if (authToken) {
      console.log('Токен получен из localStorage:', localStorage.getItem('token'));
      email=localStorage.getItem('email')
      document.getElementById('in').textContent=email;
      activate(email);
      paginationBTN();
      editPaginationBTN();
      loadDebitAccounts()
      loadCreditAccounts()
    } else {
      console.log('Токен не найден в localStorage.');
      //window.location.href = '../pages/login.html'
    }
});

function goLeft(flag){
  if(flag == 0){
    currentPage = 1;
    loadDebitAccounts();
  }else if (currentPage - flag > 0){
    currentPage -= flag;
    loadDebitAccounts();
  }
}
function goRight(flag){
  if(flag == 0){
    currentPage = pagecount;
    loadDebitAccounts();
  }else if (currentPage + flag <= pagecount){
    currentPage += flag;
    loadDebitAccounts();
  }
  
}

function paginationBTN(){
  const button1 = document.createElement('button');
  button1.textContent = '<<';
  button1.id = 'button1';
  button1.addEventListener("click", ()=>{goLeft(0)});
  const button2 = document.createElement('button');
  button2.textContent = currentPage - 2;
  button2.id = 'button2';
  button2.addEventListener("click", ()=>{goLeft(2)});
  const button3 = document.createElement('button');
  button3.textContent = currentPage - 1;
  button3.id = 'button3';
  button3.addEventListener("click", ()=>{goLeft(1)});
  const button4 = document.createElement('button');
  button4.textContent = currentPage;
  button4.id = 'button4';
  const button5 = document.createElement('button');
  button5.textContent = currentPage +1;
  button5.id = 'button5';
  button5.addEventListener("click", ()=>{goRight(1)});
  const button6 = document.createElement('button');
  button6.textContent = currentPage + 2;
  button6.id = 'button6';
  button6.addEventListener("click", ()=>{goRight(2)});
  const button7 = document.createElement('button');
  button7.textContent = '>>';
  button7.id = 'button7';
  button7.addEventListener("click", ()=>{goRight(0)});
  PaginachionContainer.appendChild(button1);
  PaginachionContainer.appendChild(button2);
  PaginachionContainer.appendChild(button3);
  PaginachionContainer.appendChild(button4);
  PaginachionContainer.appendChild(button5);
  PaginachionContainer.appendChild(button6);
  PaginachionContainer.appendChild(button7);
}

function editPaginationBTN(){
  if (currentPage + 1 < pagecount){
    document.getElementById('button6').textContent=currentPage + 2; 
    document.getElementById('button6').style.display = 'inline-block';
  }else{
    document.getElementById('button6').style.display='none';
  }
  if (currentPage < pagecount){
    document.getElementById('button5').textContent=currentPage + 1; 
    document.getElementById('button5').style.display = 'inline-block';
  }else{
    document.getElementById('button5').style.display='none';
  }
  document.getElementById('button4').textContent=currentPage; 
  if (currentPage > 1){
    document.getElementById('button3').textContent=currentPage - 1; 
    document.getElementById('button3').style.display = 'inline-block';
  }else{
    document.getElementById('button3').style.display='none';
  }
  if (currentPage > 2){
    document.getElementById('button2').textContent=currentPage - 2; 
    document.getElementById('button2').style.display = 'inline-block';
  }else{
    document.getElementById('button2').style.display='none';
  }
}

function formatDate(dateString) { 
  const date = new Date(dateString); 
  const year = date.getFullYear(); 
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0'); 
 
  return `${year}-${month}-${day}`; 
} 
 

function loadDebitAccounts() {

  DebitAccountContainer.innerHTML = '';
  pageSize = document.getElementById('Accountcount').value;

  fetch('http://localhost:8080/DebitAccounts', { 
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
    pagecount = data.totalPagesCount;
    //const DebitAccount = data.list;*/
    data.forEach(accounts => {
      const DebitAccountBlock = DebitAccountTemplate.content.cloneNode(true);
      
      DebitAccountBlock.querySelector('.description').textContent =  `Описание: ${accounts.reason}`;
      DebitAccountBlock.querySelector('.type').textContent = `Скидка: ${accounts.value}  ${accounts.type} на плотформе ${accounts.platformFor}`;
      DebitAccountBlock.querySelector('.startDate').textContent =  `Дата начала: ${formatDate(accounts.start_date)}`;
      DebitAccountBlock.querySelector('.endDate').textContent =  `Дата окончания: ${formatDate(accounts.end_date)}`;
      DebitAccountBlock.querySelector('.DebitAccount-block').addEventListener('click', () => { 
        localStorage.setItem('selectedAccount', accounts.id); 
        window.location.href = '../pages/account.html'; 
      });
      DebitAccountContainer.appendChild(DebitAccountBlock);     
    });
    editPaginationBTN();
  })
  .catch(error => { 
    console.error('Ошибка получения списка дебетовых счетов:', error); 
    alert('Ошибка получения списка дебетовых счетов: ' + error);
  }); 
}

function loadCreditAccount() {

  CreditAccountContainer.innerHTML = '';

  fetch('http://localhost:8080/CreditAccount', { 
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
    
    CreditAccountBlock.querySelector('.description').textContent =  `Описание: ${data.reason}`;
    CreditAccountBlock.querySelector('.type').textContent = `Скидка: ${data.value}`;
    CreditAccountBlock.querySelector('.startDate').textContent =  `Дата начала: ${formatDate(data.start_date)}`;
    CreditAccountBlock.querySelector('.endDate').textContent =  `Дата окончания: ${formatDate(data.end_date)}`;
    CreditAccountBlock.querySelector('.CreditAccount-block').addEventListener('click', () => { 
      localStorage.setItem('selectedAccount', data.id); 
      window.location.href = '../pages/account.html'; 
    });
    CreditAccountContainer.appendChild(CreditAccountBlock);     
  })
  .catch(error => { 
    console.error('Ошибка получения информации о кредитном счёте:', error); 
    alert('Ошибка получения информации о кредитном счёте: ' + error);
  }); 
}