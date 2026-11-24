package com.ming.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户无权访问拒绝处理器
 */
@Slf4j
public class StockAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("当前用户访问资源拒绝 原因：",authException.getMessage());
//响应数据格式json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//编辑格式
        response.setCharacterEncoding("UTF-8");
        R<Object> error = R.error(ResponseCode.NOT_PERMISSION);
//响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));

    }
}
