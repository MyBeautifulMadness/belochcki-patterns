const loginButton = document.getElementById('in');
const profileButton = document.getElementById('profileButton');
const logoutButton = document.getElementById('logoutButton');
const userMenu = document.getElementById('userMenu');
let userMenuListenerAttached = false;
const modal = document.getElementById("modal");
const createBtn = document.getElementById("createTariffBtn");
const closeBtn = document.querySelector(".close");
const form = document.getElementById("createTariffForm");
let currentPage = 0;
let pageSize = 5;
let totalPages = 0;
let editingTariffId = null;

createBtn.addEventListener("click", () => {
  modal.style.display = "flex";
  const modalTitle = document.getElementById("createTitle");
  modalTitle.textContent = "Создание кредитного тарифа";
});

closeBtn.addEventListener("click", () => {
  modal.style.display = "none";
});


form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Вы не авторизованы!");
    return;
  }

  const data = {
    name: document.getElementById("name").value,
    description: document.getElementById("description").value,
    token: token,
    amountFrom: Number(document.getElementById("amountFrom").value),
    amountTo: Number(document.getElementById("amountTo").value),
    interestRate: Number(document.getElementById("interestRate").value)
  };

  try {
    let url = "http://localhost:8084/api/creditTariff/create";
    let method = "POST";

    if (editingTariffId) {
      url = `http://localhost:8084/api/creditTariff/update/${editingTariffId}`;
      method = "PUT";
    }

    const response = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        "accept": "*/*"
      },
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`Ошибка: ${text}`);
    }


    alert(editingTariffId ? "Тариф успешно обновлён!" : "Тариф успешно создан!");

    modal.style.display = "none";
    form.reset();
    editingTariffId = null;

    await loadTariffs(currentPage);

    form.querySelector("button[type='submit']").textContent = "Создать";

  } catch (error) {
    console.error(error);
    alert("Ошибка: " + error.message);
  }
});

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

async function loadTariffs(page = 0) {
  currentPage = page;

  const params = new URLSearchParams();

  const name = document.getElementById("filterName").value;
  const description = document.getElementById("filterDescription").value;
  const amountFrom = document.getElementById("filterAmountFrom").value;
  const amountTo = document.getElementById("filterAmountTo").value;
  const interestRate = document.getElementById("filterInterestRate").value;
  const sortBy = document.getElementById("sortBy").value;
  const direction = document.getElementById("direction").value;
  const sizeInput = document.getElementById("pageSize").value;

  const size = sizeInput && sizeInput > 0 ? Number(sizeInput) : 5;

  if (name) params.append("name", name);
  if (description) params.append("description", description);
  if (amountFrom) params.append("amountFrom", amountFrom);
  if (amountTo) params.append("amountTo", amountTo);
  if (interestRate) params.append("interestRate", interestRate);
  if (sortBy) params.append("sortBy", sortBy);

  params.append("direction", direction);
  params.append("page", page);
  params.append("size", size);

  try {
    const response = await fetch(
      `http://localhost:8084/api/creditTariff/getAll?${params.toString()}`,
      { headers: { "accept": "*/*" } }
    );

    if (!response.ok) throw new Error("Ошибка загрузки");

    const result = await response.json();

    renderTariffs(result.data);

    totalPages = Math.ceil(result.totalElements / size);
    renderPagination();

  } catch (error) {
    console.error(error);
    alert("Ошибка загрузки тарифов");
  }
}

document.getElementById("applyFilters").addEventListener("click", () => {
  loadTariffs(0);
});
document.getElementById("pageSize").addEventListener("change", () => {
  loadTariffs(0);
});

function renderPagination() {
  const container = document.getElementById("pagination");
  container.innerHTML = "";

  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i + 1;

    if (i === currentPage) {
      btn.classList.add("active-page");
    }

    btn.addEventListener("click", () => {
      loadTariffs(i);
    });

    container.appendChild(btn);
  }
}

function renderTariffs(tariffs) {
  const container = document.getElementById("tariffList");
  container.innerHTML = "";

  tariffs.forEach(tariff => {
    const card = document.createElement("div");
    card.classList.add("tariff-card");

    card.innerHTML = `
      <div class="tariff-header">
        <h3>${tariff.name}</h3>
        <div class="tariff-actions">
          <button class="edit-btn" data-id="${tariff.id}">✏</button>
          <button class="delete-btn" data-id="${tariff.id}">🗑</button>
        </div>
      </div>

      <p class="description">${tariff.description}</p>

      <div class="tariff-info">
        <div><strong>Сумма от:</strong> ${tariff.amountFrom.toLocaleString()}</div>
        <div><strong>Сумма до:</strong> ${tariff.amountTo.toLocaleString()}</div>
        <div><strong>Процентная ставка:</strong> ${tariff.interestRate}%</div>
      </div>
    `;

    container.appendChild(card);

    card.querySelector(".delete-btn").addEventListener("click", () => {
      deleteTariff(tariff.id);
    });

    card.querySelector(".edit-btn").addEventListener("click", () => {
      openEditModal(tariff);
    });

  });
}

window.addEventListener("load", () => {
  loadTariffs();
});

async function deleteTariff(id) {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Вы не авторизованы!");
    return;
  }

  if (!confirm("Вы уверены, что хотите удалить этот тариф?")) return;

  try {
    const response = await fetch(
      `http://localhost:8084/api/creditTariff/delete/${id}`,
      {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "accept": "*/*"
        },
        body: JSON.stringify({ token: token })
      }
    );

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`Ошибка при удалении тарифа, на данный тариф созданы кредитны`);
    }

    alert("Тариф успешно удалён!");

    const remainingItems = document.querySelectorAll(".tariff-card").length - 1;
    if (remainingItems === 0 && currentPage > 0) {
      currentPage--;
    }

    loadTariffs(currentPage);

  } catch (error) {
    console.error(error);
    alert("Ошибка: " + error.message);
  }
}


function openEditModal(tariff) {
  editingTariffId = tariff.id;

  modal.style.display = "flex";

  const modalTitle = document.getElementById("createTitle");
  modalTitle.textContent = "Изменение кредитного тарифа";

  document.getElementById("name").value = tariff.name;
  document.getElementById("description").value = tariff.description;
  document.getElementById("amountFrom").value = tariff.amountFrom;
  document.getElementById("amountTo").value = tariff.amountTo;
  document.getElementById("interestRate").value = tariff.interestRate;

  form.querySelector("button[type='submit']").textContent = "Сохранить";
}

closeBtn.addEventListener("click", () => {
  modal.style.display = "none";
  form.reset();
  editingTariffId = null;
  form.querySelector("button[type='submit']").textContent = "Создать";
});
