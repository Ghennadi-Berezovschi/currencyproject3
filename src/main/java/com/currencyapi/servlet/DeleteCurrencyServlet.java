package com.currencyapi.servlet;

import com.currencyapi.dao.CurrencyDao;
import com.currencyapi.dao.CurrencyDaoImpl;
import com.currencyapi.dto.DeleteCurrencyRequestDto;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DeleteCurrencyServlet", urlPatterns = {"/currencies/delete"})
public class DeleteCurrencyServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Received request to delete currency with ID: " );

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        DeleteCurrencyRequestDto dto = gson.fromJson(req.getReader(), DeleteCurrencyRequestDto.class);
        int id = dto.getId();

        currencyDao.delete(id);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"message\": \"Currency deleted successfully\"}");
    }
}
