package com.ming.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: Ming
 * @Description TODO
 */
@Schema(description = "TODO")
@Data
public class SysUsersDomain {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long id;

    /**
     * 账户
     */
    @Schema(description = "账户")
    private String username;


    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;
    /**
     * 真实名称
     */
    @Schema(description = "真实名称")
    private String realName;

    /**
     * 性别(1.男 2.女)
     */
    @Schema(description = "性别(1.男 2.女)")
    private Integer sex;

    /**
     * 账户状态(1.正常 2.锁定 )
     */
    @Schema(description = "账户状态(1.正常 2.锁定 )")
    private Integer status;

    /**
     * 邮箱(唯一)
     */
    @Schema(description = "邮箱(唯一)")
    private String email;

    /**
     * 创建来源
     */
    @Schema(description = "创建来源")
    private Integer createWhere;
}
