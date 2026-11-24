package com.ming.stock.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author by Ming
 * @Description 角色添加VO
 */
@Data
public class RoleAddVo {
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;
    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限ID集合
     */
    @NotEmpty(message = "权限集合不能为空")
    private List<Long> permissionsIds;

}
