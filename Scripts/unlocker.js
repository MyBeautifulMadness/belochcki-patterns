const API_BASE = "http://localhost:8085";

let currentType = "CLIENT";
let token = localStorage.getItem("token");

window.addEventListener("load", async () => {
  if (!token) {
    redirectToLogin();
    return;
  }

  const ok = await validateToken();
  if (!ok) {
    redirectToLogin();
    return;
  }

  document.getElementById("clientsTab").onclick = () => switchTab("CLIENT");
  document.getElementById("employeesTab").onclick = () => switchTab("EMPLOYEE");
  document.getElementById("logoutBtn").onclick = logout;

  loadUsers();
});

async function validateToken() {
  try {
    const res = await fetch(`${API_BASE}/api/auth/validate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
      token: localStorage.getItem('token')
    })
})
    return res.ok;
  } catch (e) {
    return false;
  }
  
}

function redirectToLogin() {
  localStorage.clear();
  window.location.href = "../pages/login.html";
}

function logout() {
  localStorage.clear();
  redirectToLogin();
}

function switchTab(type) {
  currentType = type;

  document.getElementById("clientsTab").classList.toggle("active", type === "CLIENT");
  document.getElementById("employeesTab").classList.toggle("active", type === "EMPLOYEE");

  loadUsers();
}

async function loadUsers() {
  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients`
      : `${API_BASE}/api/employees`;

  const res = await fetch(url, {
    headers: { "Authorization": token }
  });

  if (!res.ok) {
    showToast("Ошибка загрузки пользователей");
    return;
  }

  const users = await res.json();
  renderUsers(users);
}

function renderUsers(users) {
  const tbody = document.getElementById("usersTable");
  tbody.innerHTML = "";

  users.forEach(u => {
    const tr = document.createElement("tr");

    const btn = u.status === "LOCKED"
      ? `<button class="unlock" onclick="unlockUser('${u.id}')">Разблокировать</button>`
      : `<button class="unlock" onclick="lockUser('${u.id}')">Заблокировать</button>`;

    tr.innerHTML = `
      <td>${u.name}</td>
      <td>${u.login}</td>
      <td>${u.id}</td>
      <td class="status-${u.status}">${u.status}</td>
      <td>${btn}</td>
    `;

    tbody.appendChild(tr);
  });
}

async function unlockUser(id) {
  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients/${id}/unlock`
      : `${API_BASE}/api/employees/${id}/unlock`;

  const res = await fetch(url, {
    method: "PATCH",
    headers: {
      'Authorization': token,
      "Content-Type": "application/json",
    },
  });

  if (res.ok) {
    showToast("Пользователь разблокирован");
    loadUsers();
  } else {
    const err = await res.json().catch(() => ({}));
    showToast(err.message || "Ошибка при разблокировке");
  }
}

async function lockUser(id) {
  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients/${id}/lock`
      : `${API_BASE}/api/employees/${id}/lock`;

  const res = await fetch(url, {
    method: "PATCH",
    headers: {
      'Authorization': token,
      "Content-Type": "application/json",
    },
  });

  if (res.ok) {
    showToast("Пользователь заблокирован");
    loadUsers();
  } else {
    const err = await res.json().catch(() => ({}));
    showToast(err.message || "Ошибка при блокировке");
  }
}


function showToast(text) {
  const toast = document.getElementById("toast");
  toast.innerText = text;
  toast.classList.remove("hidden");

  setTimeout(() => {
    toast.classList.add("hidden");
  }, 3000);
}