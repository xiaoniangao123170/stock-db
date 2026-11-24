package com.ming.stock.controller;

import com.ming.stock.pojo.domain.PermissionTreeDomain;
import com.ming.stock.service.RoleService;
import com.ming.stock.vo.req.RoleAddVo;
import com.ming.stock.vo.req.RolePageReqVo;
import com.ming.stock.vo.req.RoleUpdateVo;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author by Ming
 * @Description 角色处理器
 */
@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询当前角色信息
     *
     * @param vo
     * @return
     */
    @PostMapping("/roles")
    public R<PageResult> quryPageRole(@RequestBody RolePageReqVo vo) {
        return roleService.quryPageRole(vo);
    }
    @GetMapping("/permissions/tree/all")
    @Operation(summary = "树状结构回显权限集合,底层通过递归获取权限数据集合")
    public R<List<PermissionTreeDomain>> getPermissionTreeAll() {
        return roleService.getPermissionTreeAll();
    }
    /**
     * 添加角色和角色关联权限
     * @param vo
     * @return
     */
    @PostMapping("/role")
    @Operation(summary = "添加角色和角色关联权限")
    public R<?> addRole(@Valid @RequestBody RoleAddVo vo) {
        return roleService.addRole(vo);
    }
    /**
     * 根据角色id查找对应的权限id集合
     * @param roleId
     * @return
     */
    @GetMapping("/role/{roleId}")
    public R<Set<String>>  getPermissionIdsByRoleId(@PathVariable("roleId") String roleId){
        return roleService.getPermissionIdsByRoleId(roleId);
    }
    /**
     * 更新角色信息，包含角色关联的权限信息
     * @param vo
     * @return
     */
    @PutMapping("/role")
    public R<String> updateRoleWithPermissions(@RequestBody RoleUpdateVo vo){
        return roleService.updateRoleWithPermissions(vo);
    }
    /**
     * 根据角色id删除角色信息
     * @param roleId
     * @return
     */
    @DeleteMapping("/role/{roleId}")
    public R<String> deleteRole(@PathVariable("roleId")Long roleId){
        return roleService.deleteRoleById(roleId);
    }
    /**
     * 更新用户的状态信息
     * @param roleId 角色id
     * @param status 状态 1.正常 0：禁用
     * @return
     */
    @PostMapping("/role/{roleId}/{status}")
    public R<String> updateRoleStatus(@PathVariable("roleId") Long roleId,@PathVariable("status") Integer status){
        return roleService.updateRoleStatus(roleId,status);
    }




}