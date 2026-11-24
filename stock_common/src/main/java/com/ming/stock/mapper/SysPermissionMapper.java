package com.ming.stock.mapper;

import com.ming.stock.pojo.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 11519
* @description 针对表【sys_permission(权限表（菜单）)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.SysPermission
*/
public interface SysPermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    // 查询全部有效权限（正常+未删除，按排序）
    List<SysPermission> selectAllPermission();

    // 根据父pid查询子权限（递归用）
    List<SysPermission> selectByPid(@Param("pid") Long pid);


    List<SysPermission> selectAll();

    /**
     * 根据权限父类id查询对应子集权限
     * @param permissionId
     * @return
     */
    int findChildrenCountByParentId(@Param("permissionId") String permissionId);
    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(@Param("userId") Long userId);

}
