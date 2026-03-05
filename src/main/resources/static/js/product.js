document.addEventListener("DOMContentLoaded", function () {

    const merchantSelect = document.getElementById("merchantSelect");
    const categorySelect = document.getElementById("categorySelect");

    const allOptions = Array.from(categorySelect.options);

    merchantSelect.addEventListener("change", function () {
        console.log("masuk merchantSelect change");
        const merchantId = this.value;

        categorySelect.innerHTML = '<option value="">-- Select Category --</option>';

        allOptions.forEach(function(option){

            const optionMerchant = option.getAttribute("data-merchant");

            if(optionMerchant === merchantId){

                categorySelect.appendChild(option.cloneNode(true));

            }

        });

    });

});