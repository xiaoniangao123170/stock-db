package com.ming.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.stock.constant.StockConstant;
import com.ming.stock.security.user.LoginUserDetail;

import com.ming.stock.security.util.JwtTokenUtil;
import com.ming.stock.vo.req.LoginReqVo;
import com.ming.stock.vo.resp.LoginRespVo;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Collection;


/**
 * @Author: Ming
 * @Description 认证过滤器 核心作用:认证用户信息 并颁发jwt票据
 */

public class JwtLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private RedisTemplate redisTemplate;
    /**
     * 自定义构造器 传入登录认证的url地址
     * @param loginUrl
     */
    public JwtLoginAuthenticationFilter(String loginUrl) {
        super(loginUrl);
    }
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试去认证的方法
     * post/myLogin
     * data: {"username":"itming","password":"123456"}
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 判断请求方法必须是post提交，且提交的数据的内容必须是application/json格式的数据
        // 修改后，兼容 application/json;charset=UTF‑8
        String contentType = request.getContentType();
        if (!request.getMethod().equalsIgnoreCase("POST")
                || !(contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE))) {
            System.out.println(request.getMethod());
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        //获取post请求ajax传入的数据流中的username和password字段
        LoginReqVo reqVo = new ObjectMapper().readValue(request.getInputStream(), LoginReqVo.class);
        //从程序执行的效率看，先进行校验码的校验，成本较低
        //补充：根据传入的rkye 从redis中获取校验码
        //校验验证码和sessionId是否有效
        String rCheckCode = (String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX +reqVo.getSessionId());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        if (rCheckCode==null || ! rCheckCode.equalsIgnoreCase(reqVo.getCode())) {
            //验证码输入错误
            R<Object> error = R.error(ResponseCode.CHECK_CODE_ERROR.getMessage());
            String jsonData = new ObjectMapper().writeValueAsString(error);
            response.getWriter().write(jsonData);
            return null;
        }
        String username = reqVo.getUsername();
        username = (username != null) ? username.trim() : "";
        String password = reqVo.getPassword();
        password = (password != null) ? password : "";
        //组装认证票据对象
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,password);
        //交给认证管理器认证票据对象
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 成功认证后调用的方法
     * 认证成功后，将认证结果设置到SecurityContextHolder中
     * @param request
     * @param response
     * @param chain 过滤器链
     * @param authResult the object returned from the <tt>attemptAuthentication</tt> 认证对象信息
     * method.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 认证成功后，将认证结果设置到SecurityContextHolder中
        LoginUserDetail principal = (LoginUserDetail) authResult.getPrincipal();
        // 从认证对象信息中获取用户名
        String username = principal.getUsername();
        // 从认证对象信息中获取用户角色 权限
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        //生成票据["P5","ROLE_ADMIN"]
        String tokenStr = JwtTokenUtil.createToken(username, authorities.toString());
        //响应数据格式json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //编码格式
        response.setCharacterEncoding("UTF-8");
        //构建响应实体对象
        LoginRespVo respVo = new LoginRespVo();
        BeanUtils.copyProperties(principal,respVo);
        respVo.setAccessToken(tokenStr);
        R<LoginRespVo> ok = R.ok(respVo);
        //响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(ok));
        System.out.println("认证成功");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //响应数据格式json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //编码格式
        response.setCharacterEncoding("UTF-8");
        R<Object> error = R.error(ResponseCode.ERROR);
        //响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
