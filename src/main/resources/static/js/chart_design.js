var chBar = document.getElementById("chBar");
if (chBar) {
    new Chart(chBar, {
        type: 'bar',
        data: {
            // values on X-Axis
            labels: ['2021', '2022', '2023', '2024'],
            datasets: [
                {
                    label: 'Jumlah Kegiatan',
                    data: [6, 9, 10, 12],
                    backgroundColor: ['rgba(255, 99, 132, 0.2)', 'rgba(255, 159, 64, 0.2)', 'rgba(255, 205, 86, 0.2)', 'rgba(75, 192, 192, 0.2)'],
                    borderColor: ['rgb(255, 99, 132)', 'rgb(255, 159, 64)', 'rgb(255, 205, 86)', 'rgb(75, 192, 192)'],
                    borderWidth: 1,
                },
            ],
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                },
            },
        },
    });
}

var chBarFinancial = document.getElementById("chBarFinancial");
if (chBarFinancial) {
    new Chart(chBarFinancial, {
        type: 'bar',
        data: {
            // values on X-Axis
            labels: ['2021', '2022', '2023', '2024'],
            datasets: [
                {
                    label: 'Saldo (dalam juta)',
                    data: [70, 100, 110, 130],
                    backgroundColor: ['rgba(255, 99, 132, 0.2)', 'rgba(255, 159, 64, 0.2)', 'rgba(255, 205, 86, 0.2)', 'rgba(75, 192, 192, 0.2)'],
                    borderColor: ['rgb(255, 99, 132)', 'rgb(255, 159, 64)', 'rgb(255, 205, 86)', 'rgb(75, 192, 192)'],
                    borderWidth: 1,
                },
            ],
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                },
            },
        },
    });
}

var chPieAlumni = document.getElementById("chPieAlumni");
if (chPieAlumni) {
    new Chart('chPieAlumni', {
        type: 'pie',
        data: {
            // values on X-Axis
            labels: ['Jakarta', 'Jawa timur', 'Jawa Tengah', 'Jawa Barat'],
            datasets: [
                {
                    label: 'Total Members',
                    data: [200, 140, 60, 32],
                    backgroundColor: ['rgb(255, 99, 132)', 'rgb(54, 162, 235)', 'rgb(70, 200, 130)', 'rgb(255, 205, 86)'],
                    hoverOffset: 4,
                },
            ],
        },
        options: {
            aspectRatio: 1.3,
        },
    });
}

$(document).ready(function() {
    $('#province').change(function() {
        console.log("province change");
        var provinceId = $(this).val();
        console.log("provinceId=" + provinceId);
        $('#city').empty().append('<option value="">-- Pilih Kota --</option>');

        if (provinceId) {
            $.ajax({
                url: '/combo-cities',
                type: 'GET',
                data: { provinceId: provinceId },
                success: function(cities) {
                    $.each(cities, function(index, city) {
                        $('#city').append('<option value="' + city.id + '">' + city.name + '</option>');
                        /*<option th:each="city : ${cities}"
                                th:value="${city.id}"
                                th:text="${city.name}">*/
                    });
                }
            });
        }
    });
});