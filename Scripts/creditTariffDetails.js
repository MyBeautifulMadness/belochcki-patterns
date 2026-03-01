const tariffId = new URLSearchParams(window.location.search).get("id");

const API_TARIFF = `http://localhost:8084/api/creditTariff/getById/${tariffId}`;
const API_CREATE = "http://localhost:8084/api/clientCredit/create";

let tariff;

const modal = document.getElementById("modal");
const takeBtn = document.getElementById("takeCreditBtn");
const confirmBtn = document.getElementById("confirmBtn");
const amountInput = document.getElementById("creditAmount");

async function loadTariff() {
  try {
    const res = await fetch(API_TARIFF);

    if (!res.ok) {
      alert("Ошибка загрузки тарифа");
      return;
    }

    tariff = await res.json();

    document.getElementById("tariffCard").innerHTML = `
      <b>${tariff.name}</b>
      <div>${tariff.description}</div>
      <div>Сумма: ${tariff.amountFrom} – ${tariff.amountTo}</div>
      <div>Процент: ${tariff.interestRate}%</div>
    `;
  } catch (e) {
    console.error(e);
    alert("Ошибка соединения с сервером");
  }
}

takeBtn.onclick = () => {
  modal.style.display = "flex";
};

confirmBtn.onclick = async () => {
  const creditAmount = Number(amountInput.value);

  if (!creditAmount || creditAmount <= 0) {
    alert("Введите корректную сумму");
    return;
  }

  if (creditAmount < tariff.amountFrom || creditAmount > tariff.amountTo) {
    alert(`Сумма должна быть от ${tariff.amountFrom} до ${tariff.amountTo}`);
    return;
  }

  const clientId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  if (!clientId || !token) {
    alert("Сессия устарела, войдите заново");
    //window.location.href = "../Pages/login.html";
    return;
  }

  const body = {
    creditTariffId: tariff.id,
    clientId: clientId,
    token: token,
    creditAmount: creditAmount
  };

  console.log("POST BODY:", body);

  try {
    const res = await fetch(API_CREATE, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(body)
    });

    if (res.ok) {
      alert("Кредит успешно оформлен!");
      window.location.href = "../Pages/main.html";
    } else {
      const err = await res.json().catch(() => ({}));
      alert(err.message || "Ошибка при оформлении кредита");
    }
  } catch (e) {
    console.error(e);
    alert("Ошибка соединения с сервером");
  }
};

loadTariff();