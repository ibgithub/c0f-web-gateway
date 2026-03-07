document.addEventListener("DOMContentLoaded", function () {

    const merchantSelect = document.getElementById("merchantSelect");
    const outletSelect = document.getElementById("outletSelect");

    const allOptions = Array.from(outletSelect.options);

    merchantSelect.addEventListener("change", function () {
        const merchantId = this.value;

        outletSelect.innerHTML = '<option value="">-- Select Outlet --</option>';

        allOptions.forEach(function(option){

            const optionMerchant = option.getAttribute("data-merchant");

            if(optionMerchant === merchantId){

                outletSelect.appendChild(option.cloneNode(true));

            }

        });

    });

});