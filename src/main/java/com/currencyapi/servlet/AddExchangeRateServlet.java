package com.currencyapi.servlet;

import com.currencyapi.dto.ExchangeRateRequestDto;
import com.currencyapi.service.ExchangeRateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "AddExchangeRateServlet", urlPatterns = {"/add-rate"})
public class AddExchangeRateServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    private final ExchangeRateService rateService = new ExchangeRateService();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ExchangeRateRequestDto dto = gson.fromJson(request.getReader(), ExchangeRateRequestDto.class);
        rateService.save(dto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\": \"Exchange rate added or updated successfully\"}");
    }
}
