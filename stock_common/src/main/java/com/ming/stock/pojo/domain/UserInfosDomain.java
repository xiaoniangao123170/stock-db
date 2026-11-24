package com.ming.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.Date;

/**
 * @Author: Ming
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfosDomain {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 用户ID
    private String username; // 用户名称
    private String password;//密码
    private String phone; // 手机号
    private String email; // 邮件
    private String nickName; // 昵称
    private String realName; // 真实名称
    private int sex; // 性别
    private int createWhere; //创建来源
    private int status; // 状态
	
}