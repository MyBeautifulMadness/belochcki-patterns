const API_BASE = "http://localhost:8084/api/clientCredit/getAll";
let page = 0;
const loginButton = document.getElementById('in');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const creditAmountFrom = document.getElementById("creditAmountFrom");
const creditAmountTo = document.getElementById("creditAmountTo");
const debtAmountFrom = document.getElementById("debtAmountFrom");
const debtAmountTo = document.getElementById("debtAmountTo");
const creditStatus = document.getElementById("creditStatus");
const sortBy = document.getElementById("sortBy");
const direction = document.getElementById("direction");
const pageSize = document.getElementById("pageSize");
const clientId = document.getElementById("clientId");

document.getElementById("applyFilters").onclick = () => {
  page = 0;
  loadCredits();
};

function addParam(params, key, value) {
  if (value !== undefined && value !== null && value !== "") {
    params.append(key, value);
  }
}

async function loadCredits() {
  const params = new URLSearchParams();

  addParam(params, "clientId", clientId.value);
  addParam(params, "creditAmountFrom", creditAmountFrom.value);
  addParam(params, "creditAmountTo", creditAmountTo.value);
  addParam(params, "debtAmountFrom", debtAmountFrom.value);
  addParam(params, "debtAmountTo", debtAmountTo.value);
  addParam(params, "creditStatus", creditStatus.value);
  addParam(params, "sortBy", sortBy.value);

  params.append("direction", direction.value || "asc");
  params.append("page", page);
  params.append("size", pageSize.value || 5);

  const url = `${API_BASE}?${params.toString()}`;
  console.log("GET", url);

  try {
    const res = await fetch(url);

    if (!res.ok) {
      alert("Ошибка загрузки кредитов");
      return;
    }

    const data = await res.json();

    renderCredits(data.data || []);
    renderPagination(data.totalElements, data.size, data.page);

  } catch (e) {
    console.error("Ошибка запроса:", e);
    alert("Ошибка соединения с сервером");
  }
}

function renderCredits(credits) {
  const list = document.getElementById("creditList");
  list.innerHTML = "";

  if (!credits.length) {
    list.innerHTML = "<p>Кредиты не найдены</p>";
    return;
  }

  credits.forEach(c => {
    const div = document.createElement("div");
    div.className = "credit-card";
    div.onclick = () => {
      window.location.href = `creditOperations.html?creditId=${c.id}`;
    };

    div.innerHTML = `
      <div class="credit-header">
        <h3>Кредит #${c.id}</h3>
        <span class="status ${c.creditStatus}">${c.creditStatus}</span>
      </div>

      <div class="credit-info">
        <div>Сумма кредита: ${c.creditAmount}</div>
        <div>Текущий долг: ${c.debtAmount}</div>
        <div>Дата выдачи: ${c.issueDate} ${c.issueTime || ""}</div>
      </div>
    `;

    list.appendChild(div);
  });
}

function renderPagination(totalElements, size, currentPage) {
  const pag = document.getElementById("pagination");
  pag.innerHTML = "";

  const totalPages = Math.ceil(totalElements / size);
  if (totalPages <= 1) return;

  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement("button");
    btn.innerText = i + 1;

    if (i === currentPage) {
      btn.classList.add("active-page");
    }

    btn.onclick = () => {
      page = i;
      loadCredits();
    };

    pag.appendChild(btn);
  }
}

loadCredits();

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
      alert("Необходимо войти в аккаунт");
      window.location.href = '../pages/login.html'
    }
});