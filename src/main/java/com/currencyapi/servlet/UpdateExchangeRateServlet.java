package com.currencyapi.servlet;

import com.currencyapi.dto.ExchangeRateResponseDto;
import com.currencyapi.dto.UpdateRateRequestDto;
import com.currencyapi.service.ExchangeRateService;
import com.currencyapi.util.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "PatchExchangeRateServlet", urlPatterns = {"/exchangeRate/*"})
public class UpdateExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService rateService = new ExchangeRateService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }



    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        ValidationUtils.validateCurrencyPairPath(path);


        String baseCode = path.substring(1, 4).toUpperCase();
        String targetCode = path.substring(4, 7).toUpperCase();

        UpdateRateRequestDto body = gson.fromJson(request.getReader(), UpdateRateRequestDto.class);

        ExchangeRateResponseDto dto = rateService.updateRate(baseCode, targetCode, body.getRate());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(dto));
    }
}
