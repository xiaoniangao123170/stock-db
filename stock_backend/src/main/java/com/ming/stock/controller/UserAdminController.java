package com.ming.stock.controller;

import com.ming.stock.pojo.domain.SysUsersDomain;
import com.ming.stock.pojo.domain.UserInfosDomain;
import com.ming.stock.pojo.domain.UserPageDomain;
import com.ming.stock.pojo.domain.UserRoles;
import com.ming.stock.service.UserAdminService;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description TODO
 */
@RestController
@RequestMapping("/api")
public class UserAdminController {
    @Autowired
    private UserAdminService userService;
    /**
     * 多条件综合查询用户分页信息
     * @param userPageDomain
     * @return
     */
    @PostMapping("/users")
    public R<PageResult> getUserInfos(@RequestBody UserPageDomain userPageDomain) {
        return userService.getUserInfos(userPageDomain);
    }
    /**
     * 添加用户
     * @param userInfosDomain
     * @return
     */
    @PostMapping("/user")
    public R addUserInfos(@RequestBody UserInfosDomain userInfosDomain){
        return userService.addUserInfos(userInfosDomain);
    }
    /**
     * 获取用户具有的角色信息
     */
    @GetMapping("/user/roles/{userId}")
    public R<Map<String, Object>> getRole(@PathVariable String userId){
        return userService.getRole(userId);
    }
    /**
     * 更新用户角色信息
     * @param userRoles
     * @return
     */
    @PutMapping("/user/roles")
    public R<String> updateUserInfo(@RequestBody UserRoles userRoles){
        return userService.updateUserInfo(userRoles);

    }
    /**
     * 批量删除用户信息
     * @param userIds
     * @return
     */
    @DeleteMapping("/user")
    public R deleteUser(@RequestBody List<Long> userIds){
        return userService.deleteUser(userIds);
    }
    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/user/info/{userId}")
    public R<HashMap<String,Object>> getUserMsg(@PathVariable String userId){
        return userService.getUserMsg(userId);
    }
    /**
     * 根据id更新用户基本信息
     * @param sysUsers
     * @return
     */
    @PutMapping("/user")
    public R updateInfosById(@RequestBody SysUsersDomain sysUsers){
        return userService.updateInfosById(sysUsers);
    }

}
