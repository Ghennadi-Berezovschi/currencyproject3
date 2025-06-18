package com.currencyapi.servlet;

import com.currencyapi.dto.ExchangeRateResponseDto;
import com.currencyapi.service.ExchangeRateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/rates")
public class CurrencyGetAllRatesServlet extends HttpServlet {

    private final ExchangeRateService rateService = new ExchangeRateService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<ExchangeRateResponseDto> dtoList = rateService.getAllRates();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(gson.toJson(dtoList));
    }
}
