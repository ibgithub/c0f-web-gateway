document.addEventListener("DOMContentLoaded", function () {

    // ================= TOOLTIP =================
    var tooltipTriggerList = [].slice.call(
        document.querySelectorAll('[data-bs-toggle="tooltip"]')
    );

    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // ================= AUTO HIDE ALERT =================
    const alert = document.querySelector(".alert-success");

    if (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    }

});