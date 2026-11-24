package com.ming.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 用户无权限访问拒绝的处理器
 */
@Slf4j
public class StockAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 用户合法 但是无权限访问资源的时候的处理器
     * @param request that resulted in an <code>AccessDeniedException</code>
     * @param response so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation 拒绝的时候抛出异常
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("当前用户访问资源拒绝 原因：",accessDeniedException.getMessage());
//响应数据格式json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//编辑格式
        response.setCharacterEncoding("UTF-8");
        R<Object> error = R.error(ResponseCode.ANONMOUSE_NOT_PERMISSION);
//响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));

    }

}
