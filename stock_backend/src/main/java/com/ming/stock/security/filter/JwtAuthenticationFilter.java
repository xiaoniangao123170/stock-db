package com.ming.stock.security.filter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ming.stock.security.util.JwtTokenUtil;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @Author: Ming
 * @Description: 定义授权过滤器 本质就是一切的请求 获取请求头token然后进行校验
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * 过滤器的执行方法
     * @param request
     * @param response
     * @param filterChain  过滤器链
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取http请求投中携带jwt票据字符串(如果用户尚未真正 则jwt票据字符串不存在)
        String jwtToken = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        //判断请求中的票据是否存在
        if (StringUtils.isNotBlank(jwtToken)) {
            //票据不为空 解析组装认证对象
            //校验票据
            Claims claims = JwtTokenUtil.checkJWT(jwtToken);
            //票据失败
            if (claims==null){
                //如果票据失败提示前端票据已经失败需要重新认证
                R<Object> error = R.error(ResponseCode.INVALID_TOKEN);
                String respStr = new ObjectMapper().writeValueAsString(error);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(respStr);
                return;
            }
            //从合法的票据中获取用户权限信息
            //用户名
            String username = JwtTokenUtil.getUsername(jwtToken);
            //权限信息[P5,ROLE_ADMIN]
            String roles = JwtTokenUtil.getUserRole(jwtToken);
            //将数组格式的字符串转换程权限对象的集合
            String comStr = StringUtils.strip(roles,"[]");
            List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(comStr);
            //组装认证成功的票据对象
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,null,authorityList);
            //将认证成功之后的票据保存到安全框架上下文中 方便后续过滤直接获取权限信息
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
