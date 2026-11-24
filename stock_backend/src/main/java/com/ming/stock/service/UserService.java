package com.ming.stock.service;

import com.ming.stock.pojo.entity.SysUser;
import com.ming.stock.vo.req.LoginReqVo;
import com.ming.stock.vo.resp.LoginRespVo;
import com.ming.stock.vo.resp.R;

import java.util.Map;

/**
 * @Author: Ming
 * @Description 定义用户服务接口
 */
public interface UserService {
    /**
     * 根据用户名查询用户信息
     * @param userName 用户名称
     * @return
     */
    SysUser getUserByUserName(String userName);

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
    R<LoginRespVo> login(LoginReqVo vo);

    /**
     * 生成图片验证码功能
     * @return
     */
    R<Map> getCaptchaCode();
}
