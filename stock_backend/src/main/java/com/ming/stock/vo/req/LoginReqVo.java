package com.ming.stock.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: Ming
 * @Description 登录请求vo
 */
@Schema(description = "登录请求vo")
@Data
public class LoginReqVo {
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
    /**
     * 会话ID 保存在redis的那个key值
     */
    @Schema(description = "会话ID 保存在redis的那个key值")
    private String sessionId;
    //接收token
    private String accessToken;
}
