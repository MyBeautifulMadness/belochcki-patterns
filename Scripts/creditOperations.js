const API_BASE = "http://localhost:8084/api/creditOperationHistory/getAll";
let page = 0;

const creditId = new URLSearchParams(window.location.search).get("creditId");

const operationType = document.getElementById("operationType");
const dateFrom = document.getElementById("dateFrom");
const dateTo = document.getElementById("dateTo");
const amountFrom = document.getElementById("amountFrom");
const amountTo = document.getElementById("amountTo");
const sortBy = document.getElementById("sortBy");
const direction = document.getElementById("direction");
const pageSize = document.getElementById("pageSize");

document.getElementById("applyFilters").onclick = () => {
  page = 0;
  loadOperations();
};

function addParam(params, key, value) {
  if (value !== undefined && value !== null && value !== "") {
    params.append(key, value);
  }
}

async function loadOperations() {
  const params = new URLSearchParams();

  addParam(params, "clientCreditId", creditId);
  addParam(params, "operationType", operationType.value);
  addParam(params, "dateFrom", dateFrom.value);
  addParam(params, "dateTo", dateTo.value);
  addParam(params, "amountFrom", amountFrom.value);
  addParam(params, "amountTo", amountTo.value);
  addParam(params, "sortBy", sortBy.value);

  params.append("direction", direction.value || "asc");
  params.append("page", page);
  params.append("size", pageSize.value || 5);

  const url = `${API_BASE}?${params.toString()}`;
  console.log("GET", url);

  try {
    const res = await fetch(url);

    if (!res.ok) {
      alert("Ошибка загрузки операций");
      return;
    }

    const data = await res.json();
    console.log("Ответ сервера:", data);

    renderOperations(data.data || []);
    renderPagination(data.totalElements, data.size, data.page);

  } catch (e) {
    console.error("Ошибка запроса:", e);
    alert("Ошибка соединения с сервером");
  }
}

function renderOperations(ops) {
  const list = document.getElementById("operationList");
  list.innerHTML = "";

  if (!ops.length) {
    list.innerHTML = "<p>Операции не найдены</p>";
    return;
  }

  ops.forEach(o => {
    const div = document.createElement("div");
    div.className = "operation-card";

    div.innerHTML = `
      <div class="operation-header">
        <h3>${o.operationType}</h3>
        <span>${o.date} ${o.time || ""}</span>
      </div>

      <div class="operation-info">
        <div>Сумма: ${o.amount}</div>
        <div>ID кредита: ${o.clientCreditId}</div>
        <div>Комментарий: ${o.comment || "—"}</div>
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
      loadOperations();
    };

    pag.appendChild(btn);
  }
}

loadOperations();

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


function creditrepay(){
  if (document.getElementById('amount').value === '' || document.getElementById('Daccaunt').value === '') { 
    alert('Пожалуйста, укажите данные операции!'); 
    return; 
  } 
  fetch(`http://localhost:8084/api/clientCredit/repay`, { 
    method: 'POST', 
    headers: { 
      Authorization: `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json' 
    },      
    body: JSON.stringify({
      clientId: localStorage.getItem('userId'),
      token: localStorage.getItem('token'),
      creditId: creditId,
      debitAccountId: document.getElementById('Daccaunt').value,
      amount: document.getElementById('amount').value,
      comment: "Мы отберём у вас все деньги"
    })
  })
  .then(response => { 
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text) }); 
    } 
    href="../Pages/clientcredits.html"
    return response.json();
  }) 
  .catch(error => { 
    console.error('Ошибка списания с кредитного счета:', error); 
    href="../Pages/clientcredits.html"
    //alert('Ошибка списания с кредитного счета: ' + error);
  }); 
}