package com.ming.stock.security.config;

import com.ming.stock.security.filter.JwtAuthenticationFilter;
import com.ming.stock.security.filter.JwtLoginAuthenticationFilter;
import com.ming.stock.security.handler.StockAccessDeniedHandler;
import com.ming.stock.security.handler.StockAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//开启web安全设置生效
@EnableMethodSecurity //开启方法级授权注解
public class SecurityConfig{
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定义公共的无需被拦截的资源
     * @return
     */
    private String[] getPubPath(){
        //公共访问资源
        String[] urls = {
                "/*.css",
                "/*.js",
                "/favicon.ico","/doc.html",
                "/druid/**","/webjars/**","/v2/api-docs","/api/captcha",
                "/swagger/**","/swagger-resources/**","/swagger-ui.html"
        };
        return urls;
    }

    /**
     * 定义安全过滤链（替代 WebSecurityConfigurerAdapter#configure）
     * @param http
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter) throws Exception {
        //登出功能（6.1 起推荐 Lambda DSL）
        http.logout(logout -> logout.logoutUrl("/api/logout").invalidateHttpSession(true));
        //开启允许iframe 嵌套。security默认禁用ifram跨域与缓存
        http.headers(headers -> headers
                .frameOptions(frame -> frame.disable())
                .cacheControl(cache -> cache.disable()));
        //session禁用（无状态）
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());//禁用跨站请求伪造
        //对资源进行认证处理（Security 6 替代 authorizeRequests）
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(getPubPath()).permitAll()//公共资源都允许访问（Security 6 用 requestMatchers）
                .anyRequest().authenticated()  //除了上述资源外，其它资源，只有 认证通过后，才能有权访问
        );
        //添加自定义认证过滤器 并且在默认的认证过滤器之前执行
        http.addFilterBefore(jwtLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //配置授权过滤器 他是资源安全的屏障 优先级最高 所以要在自定义认证过滤器之前执行
        http.addFilterBefore(jwtAuthenticationFilter(), JwtLoginAuthenticationFilter.class);
////        //配置访问拒绝处理类
        http.exceptionHandling(access -> access.accessDeniedHandler(new StockAccessDeniedHandler()));
////        //配置没有认证的用户访问资源的时候拒绝的处理器
        http.exceptionHandling(auth -> auth.authenticationEntryPoint(new StockAuthenticationEntryPoint()));

        return http.build();
    }
    @Bean
    public JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        JwtLoginAuthenticationFilter authenticationFilter = new JwtLoginAuthenticationFilter("/api/login");
        authenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        authenticationFilter.setRedisTemplate(redisTemplate);
        return authenticationFilter;
    }
    /**
     * 自定义授权过滤器
     * @return
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }
    @Bean
    public PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}