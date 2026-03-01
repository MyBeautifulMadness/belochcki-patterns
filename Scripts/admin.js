const API_BASE = "http://localhost:8085";

let currentType = "CLIENT";
let token = localStorage.getItem("token");

let editMode = false;
let editId = null;

window.addEventListener("load", async () => {
  if (!token) return redirectToLogin();

  const ok = await validateToken();
  if (!ok) return redirectToLogin();

  document.getElementById("clientsTab").onclick = () => switchTab("CLIENT");
  document.getElementById("employeesTab").onclick = () => switchTab("EMPLOYEE");
  document.getElementById("logoutButton").onclick = logout;

  loadUsers();
});

async function validateToken() {
  try {
    const res = await fetch(`${API_BASE}/api/auth/validate`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token })
    });
    return res.ok;
  } catch {
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
    headers: { Authorization: token }
  });

  if (!res.ok) return showToast("Ошибка загрузки");

  const users = await res.json();
  renderUsers(users);
}

function renderUsers(users) {
  const tbody = document.getElementById("usersTable");
  tbody.innerHTML = "";

  users.forEach(u => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${u.name}</td>
      <td>${u.login}</td>
      <td>${u.id}</td>
      <td class="status-${u.status}">${u.status}</td>
      <td class="actions-col">
        <button onclick='openEditModal(${JSON.stringify(u)})'>✏</button>
        <button onclick="deleteUser('${u.id}')">🗑</button>
        ${
          u.status === "LOCKED"
            ? `<button onclick="unlockUser('${u.id}')">🔓</button>`
            : `<button onclick="lockUser('${u.id}')">🔒</button>`
        }
      </td>
    `;

    tbody.appendChild(tr);
  });
}


function openCreateModal() {
  editMode = false;
  editId = null;

  document.getElementById("modalTitle").innerText = "Создать";

  hideEditFields();
  clearForm();

  document.getElementById("userModal").classList.remove("hidden");
}

function openEditModal(user) {
  editMode = true;
  editId = user.id;

  document.getElementById("modalTitle").innerText = "Редактировать";

  showEditFields();

  userId.value = user.id;
  userName.value = user.name;
  userLogin.value = user.login;
  userPassword.value = "";
  userStatus.value = user.status;
  userToken.value = user.token || "";

  document.getElementById("userModal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("userModal").classList.add("hidden");
}

async function saveUser() {
  const payload = editMode
    ? {
        id: userId.value,
        name: userName.value,
        login: userLogin.value,
        password: userPassword.value,
        status: userStatus.value,
        token: userToken.value
      }
    : {
        name: userName.value,
        login: userLogin.value,
        password: userPassword.value
      };

  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients${editMode ? "/" + editId : ""}`
      : `${API_BASE}/api/employees${editMode ? "/" + editId : ""}`;

  const res = await fetch(url, {
    method: editMode ? "PUT" : "POST",
    headers: {
      Authorization: token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    closeModal();
    loadUsers();
    showToast("Сохранено");
  } else {
    showToast("Ошибка сохранения");
  }
}

async function deleteUser(id) {
  if (!confirm("Удалить пользователя?")) return;

  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients/${id}`
      : `${API_BASE}/api/employees/${id}`;

  const res = await fetch(url, {
    method: "DELETE",
    headers: { Authorization: token }
  });

  res.ok ? loadUsers() : showToast("Ошибка удаления");
}

async function lockUser(id) {
  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients/${id}/lock`
      : `${API_BASE}/api/employees/${id}/lock`;

  await fetch(url, { method: "PATCH", headers: { Authorization: token } });
  loadUsers();
}

async function unlockUser(id) {
  const url =
    currentType === "CLIENT"
      ? `${API_BASE}/api/clients/${id}/unlock`
      : `${API_BASE}/api/employees/${id}/unlock`;

  await fetch(url, { method: "PATCH", headers: { Authorization: token } });
  loadUsers();
}

/* ===== helpers ===== */

function hideEditFields() {
  userId.style.display = "none";
  userStatus.style.display = "none";
  userToken.style.display = "none";
}

function showEditFields() {
  userId.style.display = "block";
  userStatus.style.display = "block";
  userToken.style.display = "block";
}

function clearForm() {
  userId.value = "";
  userName.value = "";
  userLogin.value = "";
  userPassword.value = "";
  userStatus.value = "";
  userToken.value = "";
}

function showToast(text) {
  const toast = document.getElementById("toast");
  toast.innerText = text;
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 3000);
}