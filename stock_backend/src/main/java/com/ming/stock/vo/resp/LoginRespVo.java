package com.ming.stock.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ming.stock.pojo.domain.PermissionTreeDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Ming
 * @Description TODO
 */
@Schema(description = "TODO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRespVo {
    /**
     * 用户id
     * 将Long类型进行json格式转化 转成String格式
     */
    @Schema(description = "用户id 将Long类型进行json格式转化 转成String格式")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 电话
     */
    @Schema(description = "电话")
    private String phone;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;
    /**
     * 菜单树
     */
    private List<PermissionTreeDomain> menus;

    private List<String> permissions;

    /**
     * 认证成功后响应的jwtToken
     */
    private String accessToken;
}
