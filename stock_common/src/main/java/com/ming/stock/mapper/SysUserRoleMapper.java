package com.ming.stock.mapper;

import com.ming.stock.pojo.entity.SysRole;
import com.ming.stock.pojo.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
* @author 11519
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.SysUserRole
*/
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    /**
     * 获取用户具有的角色信息
     */
    List<Long> getRole(@Param("id") Long id);


    /**
     * 查询获得所有角色信息
     * @return
     */
    ArrayList<SysRole> getAllRole();

    /**
     * 根据用户id删除关联的角色
     * @param userId
     * @return
     */
    int deleteByUserId(@Param("userId") Long userId);
    /**
     * 批量插入信息
     * @param list
     * @return
     */
    int insertBatch(@Param("urs") List<SysUserRole> list);


}
