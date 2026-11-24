package com.ming.stock.mapper;

import com.ming.stock.pojo.domain.SysUsersDomain;
import com.ming.stock.pojo.domain.UserInfosDomain;
import com.ming.stock.pojo.domain.UserQueryDomain;
import com.ming.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
* @author 11519
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    /**
     * 根据用户名称查询用户信息
     * @param userName 用户名
     * @return         用户信息
     */
    SysUser findByUserName(@Param("userName") String userName);

    /**
     * 查询所有用户
     * @return
     */
    List<SysUser> findAll();

    /**
     * 条件综合查询用户分页信息，条件包含：分页信息 用户创建日期范围
     * @param userName
     * @param nickName
     * @param startTime
     * @param endTime
     * @return
     */
    List<UserQueryDomain> pageUsers(@Param("userName") String userName, @Param("nickName") String nickName,
                                    @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 添加用户信息
     * @param userInfosDomain
     * @return
     */
    void addUserInfos(UserInfosDomain userInfosDomain);

    /**
     * 批量删除用户信息
     * @param userIds
     * @return
     */
    void deleteUser(@Param("userIds") List<Long> userIds);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    HashMap<String, Object> getUserMsg(@Param("userId") Long userId);

    /**
     * 根据id更新用户基本信息
     * @param sysUsers
     * @return
     */
    void updateInfosById(@Param("sysUsers") SysUsersDomain sysUsers);
}
