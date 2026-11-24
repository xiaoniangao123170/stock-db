package com.ming.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "权限树形结构VO")
public class PermissionTreeDomain {
    @Schema(description = "权限id")
    private Long id;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "前端路由path")
    private String path;

    @Schema(description = "路由name")
    private String name;

    @Schema(description = "子权限集合")
    private List<PermissionTreeDomain> children;
}
