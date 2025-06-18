package com.currencyapi.servlet;

import com.currencyapi.dto.ExchangeRateResponseDto;
import com.currencyapi.service.ExchangeRateService;
import com.currencyapi.util.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "GetExchangeRateByCodeServlet", urlPatterns = {"/getRate/*"})
public class GetExchangeRateByCodeServlet extends HttpServlet {

    private final ExchangeRateService rateService = new ExchangeRateService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        ValidationUtils.validateCurrencyPairPath(path);

        String baseCode = path.substring(1, 4);
        String targetCode = path.substring(4, 7);

        ExchangeRateResponseDto dto = rateService.getRateByCodes(baseCode, targetCode);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(dto));
    }
}
