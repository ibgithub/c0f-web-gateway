document.addEventListener("DOMContentLoaded", function () {

    // TOOLTIP
    var tooltipTriggerList = [].slice.call(
        document.querySelectorAll('[data-bs-toggle="tooltip"]')
    );

    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // AUTO HIDE ALERT
    const alert = document.querySelector(".alert");

    if (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    }

});

function openCashierModal() {
    var modal = new bootstrap.Modal(document.getElementById('cashierModal'));
    modal.show();
    loadMerchants();
}

function loadMerchants() {
    console.log("masuk loadMerchants");
    fetch('/api/merchants/mine')
        .then(res => res.json())
        .then(data => {

            let select = document.getElementById("merchantSelect");
            select.innerHTML = "";

            data.forEach(m => {
                select.innerHTML += `<option value="${m.id}">${m.name}</option>`;
            });

            loadOutlets();
        });
}

function loadOutlets() {
    console.log("masuk loadOutlets");
    let merchantId = document.getElementById("merchantSelect").value;
    console.log("merchantId=" + merchantId);
    fetch(`/api/merchants/${merchantId}/outlets`)
        .then(res => res.json())
        .then(data => {
            let select = document.getElementById("outletSelect");
            select.innerHTML = "";
            data.forEach(o => {
                select.innerHTML += `<option value="${o.id}">${o.name}</option>`;
            });
        });
}

function startCashier() {

    let merchantId = document.getElementById("merchantSelect").value;
    let outletId = document.getElementById("outletSelect").value;

    window.location.href = `/cashier?merchantId=${merchantId}&outletId=${outletId}`;
}

document.addEventListener("change", function(e){
    if(e.target.id === "merchantSelect"){
        loadOutlets();
    }
});