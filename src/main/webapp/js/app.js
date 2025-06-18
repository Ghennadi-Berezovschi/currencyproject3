$(document).ready(function() {
    const host = window.location.pathname.replace(/\/index\.html$/, '');
    console.log("Host:", host);

    // -------------------- FETCH CURRENCIES --------------------
    function requestCurrencies() {
        $.ajax({
            url: `${host}/currencies`,
            type: "GET",
            dataType: "json",
            success: function (data) {
                const tbody = $('.currencies-table tbody');
                tbody.empty();
                $.each(data, function(index, currency) {
                    const row = $('<tr></tr>');
                    row.append($('<td></td>').text(currency.code));
                    row.append($('<td></td>').text(currency.name));
                    row.append($('<td></td>').text(currency.sign));
                    tbody.append(row);
                });

                const selects = [
                    "#new-rate-base-currency",
                    "#new-rate-target-currency",
                    "#convert-base-currency",
                    "#convert-target-currency"
                ];
                selects.forEach(selector => {
                    const select = $(selector);
                    select.empty();
                    $.each(data, function(index, currency) {
                        select.append(`<option value="${currency.code}">${currency.code}</option>`);
                    });
                });
            },
            error: function (jqXHR) {
                let message = "Unknown error";
                try {
                    const error = JSON.parse(jqXHR.responseText);
                    message = error.message || message;
                } catch {}
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(message);
                toast.toast("show");
            }
        });
    }

    requestCurrencies();

    // -------------------- ADD CURRENCY --------------------
    $("#add-currency").submit(function(e) {
        e.preventDefault();

        const code = $("#add-currency-code").val();
        const name = $("#add-currency-name").val();
        const sign = $("#add-currency-sign").val();

        $.ajax({
            url: `${host}/currencies/add`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                code: code,
                name: name,
                sign: sign
            }),
            success: function() {
                requestCurrencies();
            },
            error: function(jqXHR) {
                const error = JSON.parse(jqXHR.responseText || '{}');
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message || 'Unknown error');
                toast.toast("show");
            }
        });

        return false;
    });

    // -------------------- FETCH EXCHANGE RATES --------------------
    function requestExchangeRates() {
        $.ajax({
            url: `${host}/rates`,
            type: "GET",
            dataType: "json",
            success: function(response) {
                const tbody = $('.exchange-rates-table tbody');
                tbody.empty();
                $.each(response, function(index, rate) {
                    const base = rate.baseCurrency?.code || rate.baseCode || "???";
                    const target = rate.targetCurrency?.code || rate.targetCode || "???";
                    const currency = base + target;
                    const exchangeRate = rate.rate;
                    const row = $('<tr></tr>');
                    row.append($('<td></td>').text(currency));
                    row.append($('<td></td>').text(exchangeRate));
                    row.append($('<td></td>').html(
                        '<button class="btn btn-secondary btn-sm exchange-rate-edit"' +
                        'data-bs-toggle="modal" data-bs-target="#edit-exchange-rate-modal">Edit</button>'
                    ));
                    tbody.append(row);
                });
            },
            error: function(jqXHR) {
                let message = "Unknown error";
                try {
                    const error = JSON.parse(jqXHR.responseText);
                    message = error.message || message;
                } catch {}
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(message);
                toast.toast("show");
            }
        });
    }

    requestExchangeRates();

    // -------------------- EDIT MODAL LOGIC --------------------
    $(document).delegate('.exchange-rate-edit', 'click', function() {
        const pair = $(this).closest('tr').find('td:first').text();
        const exchangeRate = $(this).closest('tr').find('td:eq(1)').text();
        $('#edit-exchange-rate-modal .modal-title').text(`Edit ${pair} Exchange Rate`);
        $('#edit-exchange-rate-modal #exchange-rate-input').val(exchangeRate);
    });

    $('#edit-exchange-rate-modal .btn-primary').click(function() {
        const pair = $('#edit-exchange-rate-modal .modal-title').text().replace('Edit ', '').replace(' Exchange Rate', '');
        const exchangeRate = $('#edit-exchange-rate-modal #exchange-rate-input').val();
        const row = $(`tr:contains(${pair})`);
        row.find('td:eq(1)').text(exchangeRate);

        $.ajax({
            url: `${host}/exchangeRate/${pair}`,
            type: "PATCH",
            contentType: "application/x-www-form-urlencoded",
            data: `rate=${exchangeRate}`,
            success: function() {
                requestExchangeRates();
            },
            error: function(jqXHR) {
                let message = "Unknown error";
                try {
                    const error = JSON.parse(jqXHR.responseText);
                    message = error.message || message;
                } catch {}
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(message);
                toast.toast("show");
            }
        });

        $('#edit-exchange-rate-modal').modal('hide');
    });

    // -------------------- ADD EXCHANGE RATE --------------------
    $("#add-exchange-rate").submit(function(e) {
        e.preventDefault();

        const base = $("#new-rate-base-currency").val();
        const target = $("#new-rate-target-currency").val();
        const rate = $("#exchange-rate").val(); // ← правильно


        $.ajax({
            url: `${host}/add-rate`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                baseCurrencyCode: base,
                targetCurrencyCode: target,
                rate: rate
            }),
            success: function() {
                requestExchangeRates();
            },
            error: function(jqXHR) {
                let message = "Unknown error";
                try {
                    const error = JSON.parse(jqXHR.responseText);
                    message = error.message || message;
                } catch {}
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(message);
                toast.toast("show");
            }
        });

        return false;
    });

    // -------------------- CONVERT CURRENCY --------------------
    $("#convert").submit(function(e) {
        e.preventDefault();

        const baseCurrency = $("#convert-base-currency").val();
        const targetCurrency = $("#convert-target-currency").val();
        const amount = $("#convert-amount").val();

        $.ajax({
            url: `${host}/exchange?from=${baseCurrency}&to=${targetCurrency}&amount=${amount}`,
            type: "GET",
            success: function(data) {
                $("#convert-converted-amount").val(data.convertedAmount);
            },
            error: function(jqXHR) {
                let message = "Unknown error";
                try {
                    const error = JSON.parse(jqXHR.responseText);
                    message = error.message || message;
                } catch {}
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(message);
                toast.toast("show");
            }
        });

        return false;
    });
});
