package com.currencyapi.filter;

import com.currencyapi.dto.ErrorResponseDto;
import com.currencyapi.exception.DatabaseOperationException;
import com.currencyapi.exception.EntityExistsException;
import com.currencyapi.exception.InvalidInputException;
import com.currencyapi.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (DatabaseOperationException e) {
            writeErrorResponse(res, SC_INTERNAL_SERVER_ERROR, e);
        } catch (EntityExistsException e) {
            writeErrorResponse(res, SC_CONFLICT, e);
        } catch (InvalidInputException e) {
            writeErrorResponse(res, SC_BAD_REQUEST, e);
        } catch (NotFoundException e) {
            writeErrorResponse(res, SC_NOT_FOUND, e);
        }
    }
    private void writeErrorResponse(HttpServletResponse res, int statusCode, Exception e) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        ErrorResponseDto dto = new ErrorResponseDto(statusCode, e.getMessage());
        objectMapper.writeValue(res.getWriter(), dto);
    }



}
