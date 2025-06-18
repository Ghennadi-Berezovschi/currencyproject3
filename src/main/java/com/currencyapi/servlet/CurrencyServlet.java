package com.currencyapi.servlet;

import com.currencyapi.dto.CurrencyResponseDto;
import com.currencyapi.service.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")

public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> dtoList = currencyService.getAllCurrencies();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(gson.toJson(dtoList));
    }
}
