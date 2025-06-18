package com.currencyapi.servlet;

import com.currencyapi.dto.ExchangeResponseDto;
import com.currencyapi.service.ExchangeRateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExchangeCurrencyServlet", urlPatterns = {"/exchange"})
public class ExchangeCurrencyServlet extends HttpServlet {

    private final ExchangeRateService service = new ExchangeRateService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        ExchangeResponseDto dto = service.exchange(from, to, amount);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(dto));
    }
}
