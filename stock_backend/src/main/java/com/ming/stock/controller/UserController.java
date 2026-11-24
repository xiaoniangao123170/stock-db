package com.ming.stock.controller;

import com.ming.stock.service.UserService;
import com.ming.stock.pojo.entity.SysUser;
import com.ming.stock.vo.req.LoginReqVo;
import com.ming.stock.vo.resp.LoginRespVo;
import com.ming.stock.vo.resp.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: Ming
 * @Description 定义用户web层接口资源bean
 */
@Tag(name = "定义用户web层接口资源bean", description = "定义用户web层接口资源bean")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据用户名称查询用户信息
     * @param userName
     * @return
     */
    @Parameter(name = "userName", description = "", in = ParameterIn.PATH, required = true)
    @Operation(summary = "根据用户名称查询用户信息", description = "根据用户名称查询用户信息")
    @GetMapping("/user/{userName}")
    public SysUser getUserByUserName(@PathVariable("userName") String userName){
        return userService.getUserByUserName(userName);
    }

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
//    @Operation(summary = "用户登录功能", description = "用户登录功能")
//    @PostMapping("/login")
//    public R<LoginRespVo> login(@RequestBody LoginReqVo vo){
//        return userService.login(vo);
//    }

    /**
     * 生成图片验证码功能
     * @return
     */
    @Operation(summary = "生成图片验证码功能", description = "生成图片验证码功能")
    @GetMapping("/captcha")
    public R<Map> getCaptchaCode(){
        return userService.getCaptchaCode();
    }
}
