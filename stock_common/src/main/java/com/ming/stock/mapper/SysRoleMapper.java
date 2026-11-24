package com.ming.stock.mapper;

import com.ming.stock.pojo.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 11519
* @description 针对表【sys_role(角色表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.SysRole
*/
public interface SysRoleMapper {

    int deleteByPrimaryKey(Long id);
    // 新增角色
    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    /**
     * 分页查询当前角色信息
     * @param
     * @return
     */
    List<SysRole> selectAll();

    // 根据角色名称查重
    SysRole selectByName(@Param("name") String name);
    /**
     * 根据用户id查询角色信息
     * @param userId
     * @return
     */
    List<SysRole> getRolesByUserId(@Param("userId") Long userId);

}
