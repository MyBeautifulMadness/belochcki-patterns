const API_BASE = "http://localhost:8084/api/creditTariff/getAll";
let page = 0;

const filterName = document.getElementById("filterName");
const filterDescription = document.getElementById("filterDescription");
const amountFrom = document.getElementById("amountFrom");
const amountTo = document.getElementById("amountTo");
const interestRate = document.getElementById("interestRate");
const sortBy = document.getElementById("sortBy");
const direction = document.getElementById("direction");
const pageSize = document.getElementById("pageSize");

document.getElementById("applyFilters").onclick = () => {
  page = 0;
  loadTariffs();
};

function addParam(params, key, value) {
  if (value !== undefined && value !== null && value !== "") {
    params.append(key, value);
  }
}

async function loadTariffs() {
  const params = new URLSearchParams();

  addParam(params, "name", filterName.value);
  addParam(params, "description", filterDescription.value);
  addParam(params, "amountFrom", amountFrom.value);
  addParam(params, "amountTo", amountTo.value);
  addParam(params, "interestRate", interestRate.value);
  addParam(params, "sortBy", sortBy.value);

  params.append("direction", direction.value || "asc");
  params.append("page", page);
  params.append("size", pageSize.value || 5);

  const url = `${API_BASE}?${params.toString()}`;
  console.log("GET", url);

  try {
    const res = await fetch(url);

    if (!res.ok) {
      alert("Ошибка загрузки тарифов");
      return;
    }

    const data = await res.json();

    renderTariffs(data.data || []);
    renderPagination(data.totalElements, data.size, data.page);

  } catch (e) {
    console.error("Ошибка запроса:", e);
  }
}

function renderTariffs(tariffs) {
  const list = document.getElementById("tariffList");
  list.innerHTML = "";

  if (!tariffs.length) {
    list.innerHTML = "<p>Тарифы не найдены</p>";
    return;
  }

  tariffs.forEach(t => {
    const div = document.createElement("div");
    div.className = "tariff-card";
    div.onclick = () => {
      window.location.href = `creditTariffDetails.html?id=${t.id}`;
    };

    div.innerHTML = `
      <div class="tariff-header">
        <h3>${t.name}</h3>
      </div>

      <div class="description">${t.description}</div>

      <div class="tariff-info">
        <div>Сумма: ${t.amountFrom} – ${t.amountTo}</div>
        <div>Процент: ${t.interestRate}%</div>
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
      loadTariffs();
    };

    pag.appendChild(btn);
  }
}

loadTariffs();

