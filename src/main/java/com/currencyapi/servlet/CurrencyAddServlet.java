package com.currencyapi.servlet;

import com.currencyapi.dto.CurrencyRequestDto;
import com.currencyapi.service.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "CurrencyAddServlet", urlPatterns = {"/currencies/add"})
public class CurrencyAddServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CurrencyRequestDto dto = gson.fromJson(request.getReader(), CurrencyRequestDto.class);

        currencyService.addCurrency(dto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\": \"Currency added successfully\"}");
    }
}
