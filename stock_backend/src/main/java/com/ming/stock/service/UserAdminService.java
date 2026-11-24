package com.ming.stock.service;

import com.ming.stock.pojo.domain.SysUsersDomain;
import com.ming.stock.pojo.domain.UserInfosDomain;
import com.ming.stock.pojo.domain.UserPageDomain;
import com.ming.stock.pojo.domain.UserRoles;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description TODO
 */
public interface UserAdminService {
    /**
     * 多条件综合查询用户分页信息，条件包含：分页信息 用户创建日期范围
     * @param userPageDomain
     * @return
     */
    R<PageResult> getUserInfos(UserPageDomain userPageDomain);

    /**
     * 添加用户信息
     * @param userInfosDomain
     * @return
     */
    R addUserInfos(UserInfosDomain userInfosDomain);

    /**
     * 获取用户具有的角色信息
     */
    R<Map<String, Object>> getRole(String userId);

    /**
     * 更新用户角色信息
     * @param userRoles
     * @return
     */
    R<String> updateUserInfo(UserRoles userRoles);

    /**
     * 批量删除用户信息
     * @param userIds
     * @return
     */
    R deleteUser(List<Long> userIds);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    R<HashMap<String, Object>> getUserMsg(String userId);

    /**
     * 根据id更新用户基本信息
     * @param sysUsers
     * @return
     */
    R updateInfosById(SysUsersDomain sysUsers);
}
