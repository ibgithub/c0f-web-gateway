document.addEventListener("DOMContentLoaded", function () {

    // ================= TOOLTIP =================
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipTriggerList.forEach(el => new bootstrap.Tooltip(el));

    // ================= AUTO HIDE ALERT =================
    const alert = document.querySelector(".alert");

    if (alert) {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    }

    // ================= AUTO OPEN CASHIER MODAL =================
    if (window.showCashierModal) {
        openCashierModal();
    }

});


// ================= OPEN CASHIER MODAL =================
function openCashierModal() {

    const modalEl = document.getElementById("cashierModal");

    if (!modalEl) return;

    const modal = new bootstrap.Modal(modalEl);
    modal.show();

    loadMerchants();
}


// ================= LOAD MERCHANT =================
function loadMerchants() {

    fetch('/api/merchants/mine')
        .then(res => res.json())
        .then(data => {

            const select = document.getElementById("merchantSelect");

            if (!select) return;

            select.innerHTML = "";

            data.forEach(m => {
                select.innerHTML += `<option value="${m.id}">${m.name}</option>`;
            });

            loadOutlets();
        });
}


// ================= LOAD OUTLET =================
function loadOutlets() {

    const merchantSelect = document.getElementById("merchantSelect");
    const outletSelect = document.getElementById("outletSelect");

    if (!merchantSelect || !outletSelect) return;

    const merchantId = merchantSelect.value;

    fetch(`/api/merchants/${merchantId}/outlets`)
        .then(res => res.json())
        .then(data => {

            outletSelect.innerHTML = "";

            data.forEach(o => {
                outletSelect.innerHTML += `<option value="${o.id}">${o.name}</option>`;
            });

        });
}


// ================= START CASHIER =================
function startCashier() {

    const merchantId = document.getElementById("merchantSelect")?.value;
    const outletId = document.getElementById("outletSelect")?.value;

    if (!merchantId || !outletId) {
        alert("Merchant dan outlet harus dipilih");
        return;
    }

    fetch("/cashier/select-outlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        credentials: "same-origin",
        body: `merchantId=${merchantId}&outletId=${outletId}`
    })
        .then(() => {
            window.location.href = "/cashier";
        });

}


// ================= MERCHANT CHANGE =================
document.addEventListener("change", function(e){

    if (e.target.id === "merchantSelect") {
        loadOutlets();
    }

});