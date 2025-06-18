package com.currencyapi.servlet;

import com.currencyapi.dto.CurrencyResponseDto;
import com.currencyapi.service.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/currency/byCode/*")
public class CurrencyByCodeServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String code = (path != null && path.length() > 1) ? path.substring(1) : null;

        CurrencyResponseDto dto = currencyService.getCurrencyByCode(code);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(gson.toJson(dto));
    }
}
