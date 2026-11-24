package com.ming.stock.service;

import com.ming.stock.pojo.domain.PermissionTreeDomain;
import com.ming.stock.pojo.entity.SysPermission;
import com.ming.stock.vo.req.PermissionAddVo;
import com.ming.stock.vo.req.PermissionUpdateVo;
import com.ming.stock.vo.resp.PermissionRespNodeTreeVo;
import com.ming.stock.vo.resp.R;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Ming
 * @Description 权限服务接口
 */
public interface PermissionService {

    /**
     * 查询全部平铺权限列表（表格接口 /api/permissions）
     */
    R<List<SysPermission>> selectAllFlat();
    /**
     * 添加权限时回显权限树
     * @return
     */
    R<List<PermissionRespNodeTreeVo>> getAllPermissionTreeExBt();

    /**
     * 权限添加按钮
     *
     * @param vo
     * @return
     */
    R<String> addPermission(PermissionAddVo vo);

    /**
     * 更新权限
     *
     * @param vo
     * @return
     */
    R<String> updatePermission(PermissionUpdateVo vo);

    /**
     * 根据权限id删除权限操作（逻辑删除）
     *
     * @param permissionId
     * @return
     */
    R<String> removePermission(String permissionId);
    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(Long userId);
    /**
     * @param permissions    权限树状集合
     * @param pid            权限父id，顶级权限的pid默认为0
     * @param isOnlyMenuType true:遍历到菜单，  false:遍历到按钮
     *                       type: 目录1 菜单2 按钮3
     * @return
     */
    List<PermissionTreeDomain> getTree(List<SysPermission> permissions, String pid, boolean isOnlyMenuType);

}
