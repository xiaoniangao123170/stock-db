package com.ming.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ming.stock.mapper.SysUserMapper;
import com.ming.stock.mapper.SysUserRoleMapper;
import com.ming.stock.pojo.domain.*;
import com.ming.stock.pojo.entity.SysRole;
import com.ming.stock.pojo.entity.SysUserRole;
import com.ming.stock.service.UserAdminService;
import com.ming.stock.utils.IdWorker;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Ming
 * @Description TODO
 */
@Service
public class UserAdminServiceImpl implements UserAdminService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    /**
     * 多条件综合查询用户分页信息，条件包含：分页信息 用户创建日期范围
     * @param userPageDomain
     * @return
     */
    @Override
    public R<PageResult> getUserInfos(UserPageDomain userPageDomain) {
        //组装分页数据
        PageHelper.startPage(userPageDomain.getPageNum(),userPageDomain.getPageSize());
        //设置查询条件
        List<UserQueryDomain> users= sysUserMapper.pageUsers(userPageDomain.getUsername(),userPageDomain.getNickName(),userPageDomain.getStartTime(),userPageDomain.getEndTime());
        if (CollectionUtils.isEmpty(users)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        PageResult<UserQueryDomain> pageResult = new PageResult<>(new PageInfo<>(users));
        return R.ok(pageResult);

    }

    /**
     * 添加用户信息
     * @param userInfosDomain
     * @return
     */
    @Override
    public R addUserInfos(UserInfosDomain userInfosDomain) {
        userInfosDomain.setId(idWorker.nextId());
        sysUserMapper.addUserInfos(userInfosDomain);
        return R.ok();
    }


    /**
     * 获取用户具有的角色信息
     */
    @Override
    public R<Map<String, Object>> getRole(String userId) {
        Long id = Long.valueOf(userId);
        // 查询获得用户角色
        List<Long> userRoleList =  sysUserRoleMapper.getRole(id);
        // 查询获得所有角色信息
        ArrayList<SysRole> allRoleList = sysUserRoleMapper.getAllRole();
        // 封装数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("ownRoleIds" , userRoleList);
        data.put("allRole",allRoleList);
        return R.ok(data);


    }

    /**
     * 更新用户角色信息
     * @param userRoles
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> updateUserInfo(UserRoles userRoles) {
        //1.判断用户id是否存在
        if (userRoles.getUserId()==null) {
            throw new IllegalArgumentException(ResponseCode.DATA_ERROR.getMessage());
        }
        //2.删除用户原来所拥有的角色id
        sysUserRoleMapper.deleteByUserId(userRoles.getUserId());
        //如果对应集合为空，则说明用户将所有角色都清除了
        if (CollectionUtils.isEmpty(userRoles.getRoleIds())) {
            return R.ok(ResponseCode.SUCCESS.getMessage());
        }
        //封装用户角色对象集合

        List<SysUserRole> list = userRoles.getRoleIds().stream().map(roleId -> {
            SysUserRole userRole = SysUserRole.builder().
                    userId(userRoles.getUserId()).roleId(roleId).
                    createTime(new Date()).id(idWorker.nextId()).build();
            return userRole;
        }).collect(Collectors.toList());
        //批量插入
        int count= sysUserRoleMapper.insertBatch(list);
        if (count==0) {
            throw new IllegalArgumentException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 批量删除用户信息
     * @param userIds
     * @return
     */
    @Override
    public R deleteUser(List<Long> userIds) {
        sysUserMapper.deleteUser(userIds);
        return R.ok();
    }

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @Override
    public R<HashMap<String, Object>> getUserMsg(String userId) {
        Long user_id = Long.valueOf(userId);

        // 查询用户数据
        HashMap<String,Object> user_data= sysUserMapper.getUserMsg(user_id);
        return R.ok(user_data);

    }

    /**
     * 根据id更新用户基本信息
     * @param sysUsers
     * @return
     */
    @Override
    public R updateInfosById(SysUsersDomain sysUsers) {
        sysUserMapper.updateInfosById(sysUsers);
        return R.ok();
    }

}
